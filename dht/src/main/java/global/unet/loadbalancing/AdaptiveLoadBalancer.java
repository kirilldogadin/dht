package global.unet.loadbalancing;

import global.unet.domain.structures.NodeInfo;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Адаптивный балансировщик нагрузки для DHT сети
 */
public class AdaptiveLoadBalancer implements LoadBalancer {
    
    private final Map<NodeInfo, NodeLoadMetrics> nodeMetrics = new ConcurrentHashMap<>();
    private final Map<NodeInfo, AtomicLong> nodeRequestCounts = new ConcurrentHashMap<>();
    private final Map<OperationType, AtomicLong> operationCounts = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // Кэш для результатов
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    // Конфигурация
    private LoadBalancingConfig config;
    
    // Статистика
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong balancedRequests = new AtomicLong(0);
    private final AtomicLong rejectedRequests = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong responseTimeCount = new AtomicLong(0);
    
    // Состояние
    private volatile boolean running = false;
    private ScheduledFuture<?> metricsUpdateTask;
    private ScheduledFuture<?> cacheCleanupTask;
    
    // Стратегии балансировки
    private int roundRobinIndex = 0;
    private final Random random = new Random();
    
    public AdaptiveLoadBalancer() {
        this.config = new LoadBalancingConfig();
        
        // Инициализируем счетчики операций
        for (OperationType operation : OperationType.values()) {
            operationCounts.put(operation, new AtomicLong(0));
        }
    }
    
    @Override
    public void start() {
        if (running) return;
        
        running = true;
        
        // Запускаем периодическое обновление метрик
        metricsUpdateTask = scheduler.scheduleAtFixedRate(
            this::updateMetrics,
            0,
            config.getMetricsUpdateIntervalMs(),
            TimeUnit.MILLISECONDS
        );
        
        // Запускаем очистку кэша
        if (config.isCachingEnabled()) {
            cacheCleanupTask = scheduler.scheduleAtFixedRate(
                this::cleanupCache,
                config.getCacheTtlMs(),
                config.getCacheTtlMs(),
                TimeUnit.MILLISECONDS
            );
        }
        
        System.out.println("AdaptiveLoadBalancer started");
    }
    
    @Override
    public void stop() {
        if (!running) return;
        
        running = false;
        
        if (metricsUpdateTask != null) {
            metricsUpdateTask.cancel(false);
        }
        
        if (cacheCleanupTask != null) {
            cacheCleanupTask.cancel(false);
        }
        
        scheduler.shutdown();
        
        System.out.println("AdaptiveLoadBalancer stopped");
    }
    
    @Override
    public NodeInfo selectNode(OperationType operation, String key, List<NodeInfo> candidateNodes) {
        if (candidateNodes.isEmpty()) {
            return null;
        }
        
        totalRequests.incrementAndGet();
        operationCounts.get(operation).incrementAndGet();
        
        // Проверяем кэш для операций чтения
        if (config.isCachingEnabled() && operation.isCacheable() && key != null) {
            CacheEntry cached = cache.get(key);
            if (cached != null && !cached.isExpired()) {
                cacheHits.incrementAndGet();
                return cached.getNode();
            } else {
                cacheMisses.incrementAndGet();
            }
        }
        
        // Фильтруем перегруженные узлы
        List<NodeInfo> availableNodes = candidateNodes.stream()
            .filter(node -> !isNodeOverloaded(node))
            .collect(Collectors.toList());
        
        if (availableNodes.isEmpty()) {
            // Все узлы перегружены, используем исходный список
            availableNodes = candidateNodes;
            rejectedRequests.incrementAndGet();
        }
        
        NodeInfo selectedNode = selectNodeByStrategy(operation, availableNodes);
        
        if (selectedNode != null) {
            balancedRequests.incrementAndGet();
            
            // Обновляем счетчик запросов для узла
            nodeRequestCounts.computeIfAbsent(selectedNode, k -> new AtomicLong(0)).incrementAndGet();
            
            // Кэшируем результат для операций чтения
            if (config.isCachingEnabled() && operation.isCacheable() && key != null) {
                cache.put(key, new CacheEntry(selectedNode, System.currentTimeMillis() + config.getCacheTtlMs()));
            }
        }
        
        return selectedNode;
    }
    
    private NodeInfo selectNodeByStrategy(OperationType operation, List<NodeInfo> nodes) {
        switch (config.getStrategy()) {
            case ROUND_ROBIN:
                return selectRoundRobin(nodes);
            
            case WEIGHTED_ROUND_ROBIN:
                return selectWeightedRoundRobin(nodes);
            
            case LEAST_CONNECTIONS:
                return selectLeastConnections(nodes);
            
            case LEAST_RESPONSE_TIME:
                return selectLeastResponseTime(nodes);
            
            case LEAST_LOAD:
                return selectLeastLoad(nodes);
            
            case RANDOM:
                return selectRandom(nodes);
            
            case WEIGHTED_RANDOM:
                return selectWeightedRandom(nodes);
            
            case ADAPTIVE:
                return selectAdaptive(operation, nodes);
            
            default:
                return selectLeastLoad(nodes);
        }
    }
    
    private NodeInfo selectRoundRobin(List<NodeInfo> nodes) {
        if (nodes.isEmpty()) return null;
        
        synchronized (this) {
            NodeInfo selected = nodes.get(roundRobinIndex % nodes.size());
            roundRobinIndex = (roundRobinIndex + 1) % nodes.size();
            return selected;
        }
    }
    
    private NodeInfo selectWeightedRoundRobin(List<NodeInfo> nodes) {
        // Упрощенная реализация - используем обратную нагрузку как вес
        return nodes.stream()
            .min(Comparator.comparingDouble(node -> {
                NodeLoadMetrics metrics = nodeMetrics.get(node);
                return metrics != null ? metrics.getOverallLoad() : 0.0;
            }))
            .orElse(null);
    }
    
    private NodeInfo selectLeastConnections(List<NodeInfo> nodes) {
        return nodes.stream()
            .min(Comparator.comparingInt(node -> {
                NodeLoadMetrics metrics = nodeMetrics.get(node);
                return metrics != null ? metrics.getActiveConnections() : 0;
            }))
            .orElse(null);
    }
    
    private NodeInfo selectLeastResponseTime(List<NodeInfo> nodes) {
        return nodes.stream()
            .min(Comparator.comparingDouble(node -> {
                NodeLoadMetrics metrics = nodeMetrics.get(node);
                return metrics != null ? metrics.getAverageResponseTime() : 0.0;
            }))
            .orElse(null);
    }
    
    private NodeInfo selectLeastLoad(List<NodeInfo> nodes) {
        return nodes.stream()
            .min(Comparator.comparingDouble(node -> {
                NodeLoadMetrics metrics = nodeMetrics.get(node);
                return metrics != null ? metrics.getOverallLoad() : 0.0;
            }))
            .orElse(null);
    }
    
    private NodeInfo selectRandom(List<NodeInfo> nodes) {
        if (nodes.isEmpty()) return null;
        return nodes.get(random.nextInt(nodes.size()));
    }
    
    private NodeInfo selectWeightedRandom(List<NodeInfo> nodes) {
        if (nodes.isEmpty()) return null;
        
        // Вычисляем веса (обратная нагрузка)
        double totalWeight = 0.0;
        Map<NodeInfo, Double> weights = new HashMap<>();
        
        for (NodeInfo node : nodes) {
            NodeLoadMetrics metrics = nodeMetrics.get(node);
            double weight = metrics != null ? (1.0 - metrics.getOverallLoad()) : 1.0;
            weights.put(node, weight);
            totalWeight += weight;
        }
        
        if (totalWeight == 0.0) {
            return selectRandom(nodes);
        }
        
        // Выбираем случайный узел с учетом весов
        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0.0;
        
        for (NodeInfo node : nodes) {
            currentWeight += weights.get(node);
            if (randomValue <= currentWeight) {
                return node;
            }
        }
        
        return nodes.get(nodes.size() - 1);
    }
    
    private NodeInfo selectAdaptive(OperationType operation, List<NodeInfo> nodes) {
        // Адаптивная стратегия выбирает лучший метод на основе текущих условий
        
        // Для операций чтения предпочитаем узлы с низким временем отклика
        if (operation == OperationType.FIND_VALUE || operation == OperationType.FIND_NODE) {
            return selectLeastResponseTime(nodes);
        }
        
        // Для операций записи предпочитаем узлы с низкой нагрузкой
        if (operation == OperationType.STORE || operation == OperationType.REPLICATE) {
            return selectLeastLoad(nodes);
        }
        
        // Для остальных операций используем комбинированный подход
        return selectLeastLoad(nodes);
    }
    
    @Override
    public List<NodeInfo> selectNodes(OperationType operation, String key, List<NodeInfo> candidateNodes, int count) {
        List<NodeInfo> selectedNodes = new ArrayList<>();
        List<NodeInfo> remainingNodes = new ArrayList<>(candidateNodes);
        
        for (int i = 0; i < count && !remainingNodes.isEmpty(); i++) {
            NodeInfo selected = selectNode(operation, key, remainingNodes);
            if (selected != null) {
                selectedNodes.add(selected);
                remainingNodes.remove(selected);
            }
        }
        
        return selectedNodes;
    }
    
    @Override
    public void updateNodeLoad(NodeInfo node, NodeLoadMetrics loadMetrics) {
        nodeMetrics.put(node, loadMetrics);
        
        // Обновляем статистику времени отклика
        if (loadMetrics.getAverageResponseTime() > 0) {
            totalResponseTime.addAndGet((long) loadMetrics.getAverageResponseTime());
            responseTimeCount.incrementAndGet();
        }
    }
    
    @Override
    public NodeLoadMetrics getNodeLoad(NodeInfo node) {
        return nodeMetrics.getOrDefault(node, NodeLoadMetrics.empty());
    }
    
    @Override
    public boolean isNodeOverloaded(NodeInfo node) {
        NodeLoadMetrics metrics = nodeMetrics.get(node);
        if (metrics == null) return false;
        
        // Проверяем, не устарели ли метрики
        if (metrics.isStale(config.getMetricsMaxAgeMs())) {
            return false;
        }
        
        return metrics.getOverallLoad() > config.getOverloadThreshold();
    }
    
    @Override
    public List<NodeInfo> getOverloadedNodes() {
        return nodeMetrics.entrySet().stream()
            .filter(entry -> {
                NodeLoadMetrics metrics = entry.getValue();
                return !metrics.isStale(config.getMetricsMaxAgeMs()) &&
                       metrics.getOverallLoad() > config.getOverloadThreshold();
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<NodeInfo> getUnderloadedNodes() {
        return nodeMetrics.entrySet().stream()
            .filter(entry -> {
                NodeLoadMetrics metrics = entry.getValue();
                return !metrics.isStale(config.getMetricsMaxAgeMs()) &&
                       metrics.getOverallLoad() < config.getUnderloadThreshold();
            })
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    @Override
    public LoadBalancingStats getStats() {
        // Подготавливаем статистику по операциям
        Map<String, Long> requestsByOperation = operationCounts.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().getName(),
                entry -> entry.getValue().get()
            ));
        
        // Подготавливаем статистику по узлам
        Map<String, Long> requestsByNode = nodeRequestCounts.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().toString(),
                entry -> entry.getValue().get()
            ));
        
        // Вычисляем среднее время отклика
        double avgResponseTime = responseTimeCount.get() > 0 ? 
            (double) totalResponseTime.get() / responseTimeCount.get() : 0.0;
        
        // Вычисляем дисперсию распределения нагрузки
        double loadVariance = calculateLoadDistributionVariance();
        
        // Подсчитываем перегруженные и недогруженные узлы
        int overloadedNodes = getOverloadedNodes().size();
        int underloadedNodes = getUnderloadedNodes().size();
        int totalNodes = nodeMetrics.size();
        
        return new LoadBalancingStats(
            totalRequests.get(),
            balancedRequests.get(),
            rejectedRequests.get(),
            requestsByOperation,
            requestsByNode,
            avgResponseTime,
            loadVariance,
            overloadedNodes,
            underloadedNodes,
            totalNodes,
            cacheHits.get(),
            cacheMisses.get(),
            System.currentTimeMillis()
        );
    }
    
    private double calculateLoadDistributionVariance() {
        List<Double> loads = nodeMetrics.values().stream()
            .filter(metrics -> !metrics.isStale(config.getMetricsMaxAgeMs()))
            .map(NodeLoadMetrics::getOverallLoad)
            .collect(Collectors.toList());
        
        if (loads.size() < 2) return 0.0;
        
        double mean = loads.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = loads.stream()
            .mapToDouble(load -> Math.pow(load - mean, 2))
            .average()
            .orElse(0.0);
        
        return Math.sqrt(variance);
    }
    
    @Override
    public void configure(LoadBalancingConfig config) {
        this.config = config;
        
        // Перезапускаем задачи с новой конфигурацией
        if (running) {
            stop();
            start();
        }
    }
    
    private void updateMetrics() {
        // Удаляем устаревшие метрики
        nodeMetrics.entrySet().removeIf(entry -> 
            entry.getValue().isStale(config.getMetricsMaxAgeMs()));
    }
    
    private void cleanupCache() {
        if (!config.isCachingEnabled()) return;
        
        long currentTime = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
        
        // Ограничиваем размер кэша
        if (cache.size() > config.getCacheSize()) {
            // Удаляем самые старые записи
            cache.entrySet().stream()
                .sorted(Comparator.comparingLong(entry -> entry.getValue().getExpirationTime()))
                .limit(cache.size() - config.getCacheSize())
                .map(Map.Entry::getKey)
                .forEach(cache::remove);
        }
    }
    
    /**
     * Внутренний класс для записей кэша
     */
    private static class CacheEntry {
        private final NodeInfo node;
        private final long expirationTime;
        
        public CacheEntry(NodeInfo node, long expirationTime) {
            this.node = node;
            this.expirationTime = expirationTime;
        }
        
        public NodeInfo getNode() { return node; }
        public long getExpirationTime() { return expirationTime; }
        
        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }
        
        public boolean isExpired(long currentTime) {
            return currentTime > expirationTime;
        }
    }
}

