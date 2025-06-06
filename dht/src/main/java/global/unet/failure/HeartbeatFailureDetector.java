package global.unet.failure;

import global.unet.domain.structures.NodeInfo;
import global.unet.network.rpc.RPCClient;
import global.unet.network.rpc.DHTMessage;
import global.unet.network.rpc.MessageType;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация детектора отказов на основе heartbeat
 */
public class HeartbeatFailureDetector implements FailureDetector {
    
    private final RPCClient rpcClient;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService executor;
    
    // Конфигурация
    private FailureDetectionConfig config;
    
    // Состояние узлов
    private final Map<NodeInfo, NodeState> nodeStates = new ConcurrentHashMap<>();
    private final Set<NodeInfo> failedNodes = ConcurrentHashMap.newKeySet();
    private final Set<NodeInfo> suspiciousNodes = ConcurrentHashMap.newKeySet();
    
    // Слушатели событий
    private final List<FailureListener> listeners = new CopyOnWriteArrayList<>();
    
    // Статистика
    private final AtomicLong totalFailures = new AtomicLong(0);
    private final AtomicLong totalRecoveries = new AtomicLong(0);
    private final AtomicLong networkPartitions = new AtomicLong(0);
    private final AtomicLong totalDetectionTime = new AtomicLong(0);
    private final AtomicLong detectionCount = new AtomicLong(0);
    
    private volatile boolean running = false;
    private ScheduledFuture<?> heartbeatTask;
    
    public HeartbeatFailureDetector(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.executor = Executors.newCachedThreadPool();
        this.config = new FailureDetectionConfig();
    }
    
    @Override
    public void start() {
        if (running) return;
        
        running = true;
        
        // Запускаем периодическую проверку узлов
        heartbeatTask = scheduler.scheduleAtFixedRate(
            this::performHeartbeatCheck,
            0,
            config.getHeartbeatIntervalMs(),
            TimeUnit.MILLISECONDS
        );
        
        System.out.println("HeartbeatFailureDetector started");
    }
    
    @Override
    public void stop() {
        if (!running) return;
        
        running = false;
        
        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
        }
        
        scheduler.shutdown();
        executor.shutdown();
        
        System.out.println("HeartbeatFailureDetector stopped");
    }
    
    @Override
    public void addNode(NodeInfo node) {
        nodeStates.put(node, new NodeState(node));
        failedNodes.remove(node);
        suspiciousNodes.remove(node);
    }
    
    @Override
    public void removeNode(NodeInfo node) {
        nodeStates.remove(node);
        failedNodes.remove(node);
        suspiciousNodes.remove(node);
    }
    
    @Override
    public CompletableFuture<Boolean> checkNode(NodeInfo node) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // Отправляем PING сообщение
                DHTMessage pingMessage = new DHTMessage(
                    MessageType.PING,
                    UUID.randomUUID().toString(),
                    null,
                    null,
                    null,
                    System.currentTimeMillis()
                );
                
                DHTMessage response = rpcClient.sendMessage(pingMessage, node.getAddress(), node.getPort());
                
                long responseTime = System.currentTimeMillis() - startTime;
                
                // Обновляем состояние узла
                NodeState state = nodeStates.get(node);
                if (state != null) {
                    if (response != null && response.getType() == MessageType.PONG) {
                        state.recordSuccess(responseTime);
                        return true;
                    } else {
                        state.recordFailure();
                        return false;
                    }
                }
                
                return response != null && response.getType() == MessageType.PONG;
                
            } catch (Exception e) {
                NodeState state = nodeStates.get(node);
                if (state != null) {
                    state.recordFailure();
                }
                return false;
            }
        }, executor);
    }
    
    private void performHeartbeatCheck() {
        if (!running) return;
        
        List<CompletableFuture<Void>> checkFutures = new ArrayList<>();
        
        for (NodeInfo node : nodeStates.keySet()) {
            CompletableFuture<Void> future = checkNode(node)
                .thenAccept(isHealthy -> processNodeCheckResult(node, isHealthy));
            
            checkFutures.add(future);
        }
        
        // Ждем завершения всех проверок (с таймаутом)
        try {
            CompletableFuture.allOf(checkFutures.toArray(new CompletableFuture[0]))
                .get(config.getTimeoutMs(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // Некоторые проверки могли не завершиться
        }
    }
    
    private void processNodeCheckResult(NodeInfo node, boolean isHealthy) {
        NodeState state = nodeStates.get(node);
        if (state == null) return;
        
        boolean wasHealthy = !failedNodes.contains(node) && !suspiciousNodes.contains(node);
        
        if (isHealthy) {
            // Узел отвечает
            if (!wasHealthy) {
                // Восстановление узла
                failedNodes.remove(node);
                suspiciousNodes.remove(node);
                totalRecoveries.incrementAndGet();
                
                // Уведомляем слушателей
                for (FailureListener listener : listeners) {
                    try {
                        listener.onNodeRecovery(node);
                    } catch (Exception e) {
                        // Игнорируем ошибки в слушателях
                    }
                }
            }
        } else {
            // Узел не отвечает
            if (wasHealthy) {
                // Переводим в подозрительные
                suspiciousNodes.add(node);
            } else if (suspiciousNodes.contains(node)) {
                // Если узел уже подозрительный и снова не отвечает
                if (state.getConsecutiveFailures() >= config.getMaxRetries()) {
                    // Объявляем узел отказавшим
                    suspiciousNodes.remove(node);
                    failedNodes.add(node);
                    totalFailures.incrementAndGet();
                    
                    long detectionTime = System.currentTimeMillis() - state.getFirstFailureTime();
                    totalDetectionTime.addAndGet(detectionTime);
                    detectionCount.incrementAndGet();
                    
                    // Уведомляем слушателей
                    for (FailureListener listener : listeners) {
                        try {
                            listener.onNodeFailure(node, "Heartbeat timeout after " + config.getMaxRetries() + " retries");
                        } catch (Exception e) {
                            // Игнорируем ошибки в слушателях
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public List<NodeInfo> getFailedNodes() {
        return new ArrayList<>(failedNodes);
    }
    
    @Override
    public List<NodeInfo> getHealthyNodes() {
        List<NodeInfo> healthy = new ArrayList<>();
        for (NodeInfo node : nodeStates.keySet()) {
            if (!failedNodes.contains(node) && !suspiciousNodes.contains(node)) {
                healthy.add(node);
            }
        }
        return healthy;
    }
    
    @Override
    public void addFailureListener(FailureListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void removeFailureListener(FailureListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public FailureDetectionStats getStats() {
        int totalNodes = nodeStates.size();
        int healthyNodes = getHealthyNodes().size();
        int failed = failedNodes.size();
        int suspicious = suspiciousNodes.size();
        
        double avgDetectionTime = detectionCount.get() > 0 ? 
            (double) totalDetectionTime.get() / detectionCount.get() : 0.0;
        
        // Упрощенный расчет false positive rate
        double falsePositiveRate = 0.0; // Требует дополнительной логики
        
        return new FailureDetectionStats(
            totalNodes,
            healthyNodes,
            failed,
            suspicious,
            totalFailures.get(),
            totalRecoveries.get(),
            networkPartitions.get(),
            avgDetectionTime,
            falsePositiveRate,
            System.currentTimeMillis()
        );
    }
    
    @Override
    public void configure(FailureDetectionConfig config) {
        this.config = config;
    }
    
    /**
     * Внутренний класс для отслеживания состояния узла
     */
    private static class NodeState {
        private final NodeInfo node;
        private volatile long lastSuccessTime;
        private volatile long firstFailureTime;
        private volatile int consecutiveFailures;
        private volatile long totalResponseTime;
        private volatile int successCount;
        
        public NodeState(NodeInfo node) {
            this.node = node;
            this.lastSuccessTime = System.currentTimeMillis();
            this.firstFailureTime = 0;
            this.consecutiveFailures = 0;
            this.totalResponseTime = 0;
            this.successCount = 0;
        }
        
        public void recordSuccess(long responseTime) {
            this.lastSuccessTime = System.currentTimeMillis();
            this.consecutiveFailures = 0;
            this.firstFailureTime = 0;
            this.totalResponseTime += responseTime;
            this.successCount++;
        }
        
        public void recordFailure() {
            if (consecutiveFailures == 0) {
                this.firstFailureTime = System.currentTimeMillis();
            }
            this.consecutiveFailures++;
        }
        
        // Геттеры
        public long getLastSuccessTime() { return lastSuccessTime; }
        public long getFirstFailureTime() { return firstFailureTime; }
        public int getConsecutiveFailures() { return consecutiveFailures; }
        public double getAverageResponseTime() {
            return successCount > 0 ? (double) totalResponseTime / successCount : 0.0;
        }
    }
}

