package global.unet.failure;

import global.unet.domain.id.KademliaId;
import global.unet.domain.structures.NodeInfo;
import global.unet.domain.routing.XorTreeRoutingTable;
import global.unet.network.rpc.RPCClient;
import global.unet.network.rpc.DHTMessage;
import global.unet.network.rpc.MessageType;
import global.unet.replication.ReplicationManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Основная реализация менеджера восстановления данных
 */
public class DHTRecoveryManager implements RecoveryManager, FailureListener {
    
    private final KademliaId nodeId;
    private final XorTreeRoutingTable routingTable;
    private final RPCClient rpcClient;
    private final ReplicationManager replicationManager;
    private final FailureDetector failureDetector;
    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;
    
    // Конфигурация
    private RecoveryConfig config;
    
    // Состояние восстановления
    private final Map<NodeInfo, Set<String>> nodeKeyMap = new ConcurrentHashMap<>();
    private final Map<String, Set<NodeInfo>> keyNodeMap = new ConcurrentHashMap<>();
    private final Set<NodeInfo> nodesInRecovery = ConcurrentHashMap.newKeySet();
    private final Map<String, CompletableFuture<RecoveryResult>> activeRecoveries = new ConcurrentHashMap<>();
    
    // Статистика
    private final AtomicLong totalRecoveries = new AtomicLong(0);
    private final AtomicLong successfulRecoveries = new AtomicLong(0);
    private final AtomicLong failedRecoveries = new AtomicLong(0);
    private final AtomicLong totalKeysRecovered = new AtomicLong(0);
    private final AtomicLong totalKeysLost = new AtomicLong(0);
    private final AtomicLong totalRecoveryTime = new AtomicLong(0);
    private volatile long lastRecoveryTime = 0;
    
    private volatile boolean running = false;
    private ScheduledFuture<?> rebalanceTask;
    
    public DHTRecoveryManager(KademliaId nodeId, XorTreeRoutingTable routingTable, RPCClient rpcClient,
                            ReplicationManager replicationManager, FailureDetector failureDetector) {
        this.nodeId = nodeId;
        this.routingTable = routingTable;
        this.rpcClient = rpcClient;
        this.replicationManager = replicationManager;
        this.failureDetector = failureDetector;
        this.executor = Executors.newFixedThreadPool(4);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.config = new RecoveryConfig();
        
        // Регистрируемся как слушатель отказов
        failureDetector.addFailureListener(this);
    }
    
    @Override
    public void start() {
        if (running) return;
        
        running = true;
        
        // Запускаем периодическую перебалансировку данных
        if (config.isAutoRebalanceEnabled()) {
            rebalanceTask = scheduler.scheduleAtFixedRate(
                () -> rebalanceData(),
                config.getRebalanceIntervalMs(),
                config.getRebalanceIntervalMs(),
                TimeUnit.MILLISECONDS
            );
        }
        
        System.out.println("DHTRecoveryManager started");
    }
    
    @Override
    public void stop() {
        if (!running) return;
        
        running = false;
        
        if (rebalanceTask != null) {
            rebalanceTask.cancel(false);
        }
        
        // Отменяем активные восстановления
        for (CompletableFuture<RecoveryResult> recovery : activeRecoveries.values()) {
            recovery.cancel(true);
        }
        
        scheduler.shutdown();
        executor.shutdown();
        
        System.out.println("DHTRecoveryManager stopped");
    }
    
    @Override
    public CompletableFuture<Boolean> handleNodeFailure(NodeInfo failedNode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Handling failure of node: " + failedNode);
                
                // Проверяем, не обрабатывается ли уже этот узел
                if (nodesInRecovery.contains(failedNode)) {
                    return true;
                }
                
                nodesInRecovery.add(failedNode);
                
                // Получаем список ключей, которые были на отказавшем узле
                Set<String> affectedKeys = nodeKeyMap.getOrDefault(failedNode, new HashSet<>());
                
                if (affectedKeys.isEmpty()) {
                    // Нет данных для восстановления
                    nodesInRecovery.remove(failedNode);
                    return true;
                }
                
                // Получаем список узлов для замещения
                List<NodeInfo> replacementNodes = findReplacementNodes(failedNode, affectedKeys.size());
                
                if (replacementNodes.isEmpty()) {
                    // Нет доступных узлов для замещения
                    nodesInRecovery.remove(failedNode);
                    return false;
                }
                
                // Запускаем восстановление данных
                String recoveryId = UUID.randomUUID().toString();
                CompletableFuture<RecoveryResult> recoveryFuture = recoverData(failedNode, replacementNodes);
                activeRecoveries.put(recoveryId, recoveryFuture);
                
                recoveryFuture.whenComplete((result, ex) -> {
                    activeRecoveries.remove(recoveryId);
                    nodesInRecovery.remove(failedNode);
                    
                    if (ex != null) {
                        failedRecoveries.incrementAndGet();
                    } else if (result.isSuccess()) {
                        successfulRecoveries.incrementAndGet();
                        totalKeysRecovered.addAndGet(result.getRecoveredKeys());
                        totalKeysLost.addAndGet(result.getLostKeys());
                        totalRecoveryTime.addAndGet(result.getRecoveryTimeMs());
                        lastRecoveryTime = System.currentTimeMillis();
                    } else {
                        failedRecoveries.incrementAndGet();
                    }
                });
                
                return true;
                
            } catch (Exception e) {
                nodesInRecovery.remove(failedNode);
                return false;
            }
        }, executor);
    }
    
    private List<NodeInfo> findReplacementNodes(NodeInfo failedNode, int requiredCapacity) {
        // Получаем список здоровых узлов
        List<NodeInfo> healthyNodes = failureDetector.getHealthyNodes();
        
        // Фильтруем узлы, которые уже в процессе восстановления
        healthyNodes.removeAll(nodesInRecovery);
        
        // Сортируем по близости к ID отказавшего узла
        healthyNodes.sort((n1, n2) -> {
            KademliaId id1 = n1.getNodeId();
            KademliaId id2 = n2.getNodeId();
            KademliaId failedId = failedNode.getNodeId();
            
            return id1.distanceTo(failedId).compareTo(id2.distanceTo(failedId));
        });
        
        // Возвращаем ближайшие узлы
        return healthyNodes.subList(0, Math.min(config.getTargetReplicationFactor(), healthyNodes.size()));
    }
    
    @Override
    public CompletableFuture<Boolean> handleNodeRecovery(NodeInfo recoveredNode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Handling recovery of node: " + recoveredNode);
                
                // Добавляем узел в таблицу маршрутизации
                routingTable.addNode(recoveredNode);
                
                // Запускаем перебалансировку данных
                rebalanceData();
                
                return true;
                
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<RecoveryResult> recoverData(NodeInfo failedNode, List<NodeInfo> replacementNodes) {
        totalRecoveries.incrementAndGet();
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Recovering data from failed node: " + failedNode + " to " + replacementNodes.size() + " nodes");
                
                // Получаем список ключей, которые были на отказавшем узле
                Set<String> affectedKeys = nodeKeyMap.getOrDefault(failedNode, new HashSet<>());
                
                if (affectedKeys.isEmpty()) {
                    // Нет данных для восстановления
                    return RecoveryResult.success(0, 0, new ArrayList<>(), new ArrayList<>(),
                                               new HashMap<>(), System.currentTimeMillis() - startTime);
                }
                
                List<String> recoveredKeys = new ArrayList<>();
                List<String> lostKeys = new ArrayList<>();
                Map<NodeInfo, Integer> nodeContributions = new HashMap<>();
                
                // Для каждого ключа пытаемся найти его на других узлах и восстановить
                for (String key : affectedKeys) {
                    try {
                        // Получаем список узлов, которые имеют копию ключа
                        Set<NodeInfo> nodesWithKey = keyNodeMap.getOrDefault(key, new HashSet<>());
                        nodesWithKey.remove(failedNode);
                        
                        if (nodesWithKey.isEmpty()) {
                            // Нет других копий ключа
                            lostKeys.add(key);
                            continue;
                        }
                        
                        // Получаем значение с одного из узлов
                        String value = null;
                        NodeInfo sourceNode = null;
                        
                        for (NodeInfo node : nodesWithKey) {
                            try {
                                value = retrieveFromNode(key, node);
                                if (value != null) {
                                    sourceNode = node;
                                    break;
                                }
                            } catch (Exception e) {
                                // Пробуем следующий узел
                            }
                        }
                        
                        if (value == null) {
                            // Не удалось получить значение
                            lostKeys.add(key);
                            continue;
                        }
                        
                        // Реплицируем на новые узлы
                        boolean replicated = false;
                        for (NodeInfo node : replacementNodes) {
                            try {
                                if (replicateToNode(key, value, node)) {
                                    // Обновляем карты
                                    nodeKeyMap.computeIfAbsent(node, k -> new HashSet<>()).add(key);
                                    keyNodeMap.computeIfAbsent(key, k -> new HashSet<>()).add(node);
                                    
                                    // Обновляем статистику
                                    nodeContributions.put(node, nodeContributions.getOrDefault(node, 0) + 1);
                                    
                                    replicated = true;
                                }
                            } catch (Exception e) {
                                // Пробуем следующий узел
                            }
                        }
                        
                        if (replicated) {
                            recoveredKeys.add(key);
                            // Обновляем статистику источника
                            nodeContributions.put(sourceNode, nodeContributions.getOrDefault(sourceNode, 0) + 1);
                        } else {
                            lostKeys.add(key);
                        }
                        
                    } catch (Exception e) {
                        lostKeys.add(key);
                    }
                }
                
                // Удаляем отказавший узел из карт
                nodeKeyMap.remove(failedNode);
                for (Set<NodeInfo> nodes : keyNodeMap.values()) {
                    nodes.remove(failedNode);
                }
                
                long recoveryTime = System.currentTimeMillis() - startTime;
                
                return RecoveryResult.success(
                    recoveredKeys.size(),
                    lostKeys.size(),
                    recoveredKeys,
                    lostKeys,
                    nodeContributions,
                    recoveryTime
                );
                
            } catch (Exception e) {
                return RecoveryResult.failure(
                    "Recovery failed: " + e.getMessage(),
                    System.currentTimeMillis() - startTime
                );
            }
        }, executor);
    }
    
    private String retrieveFromNode(String key, NodeInfo node) {
        try {
            // Создаем FIND_VALUE сообщение
            DHTMessage findMessage = new DHTMessage(
                MessageType.FIND_VALUE,
                UUID.randomUUID().toString(),
                key,
                null,
                null,
                System.currentTimeMillis()
            );
            
            // Отправляем сообщение
            DHTMessage response = rpcClient.sendMessage(findMessage, node.getAddress(), node.getPort());
            
            if (response != null && response.getType() == MessageType.VALUE_RESPONSE) {
                return response.getValue();
            }
            
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve from node " + node, e);
        }
    }
    
    private boolean replicateToNode(String key, String value, NodeInfo node) {
        try {
            // Создаем STORE сообщение
            DHTMessage storeMessage = new DHTMessage(
                MessageType.STORE,
                UUID.randomUUID().toString(),
                key,
                value,
                null,
                System.currentTimeMillis()
            );
            
            // Отправляем сообщение
            DHTMessage response = rpcClient.sendMessage(storeMessage, node.getAddress(), node.getPort());
            
            return response != null && response.getType() == MessageType.STORE_RESPONSE;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public CompletableFuture<Boolean> rebalanceData() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Rebalancing data across the network");
                
                // Получаем список здоровых узлов
                List<NodeInfo> healthyNodes = failureDetector.getHealthyNodes();
                
                if (healthyNodes.size() < config.getTargetReplicationFactor()) {
                    // Недостаточно узлов для перебалансировки
                    return false;
                }
                
                // Для каждого ключа проверяем, достаточно ли реплик
                for (Map.Entry<String, Set<NodeInfo>> entry : keyNodeMap.entrySet()) {
                    String key = entry.getKey();
                    Set<NodeInfo> currentNodes = entry.getValue();
                    
                    // Удаляем отказавшие узлы
                    currentNodes.removeIf(node -> failureDetector.getFailedNodes().contains(node));
                    
                    // Если реплик недостаточно, добавляем новые
                    if (currentNodes.size() < config.getTargetReplicationFactor()) {
                        // Получаем значение с одного из существующих узлов
                        String value = null;
                        for (NodeInfo node : currentNodes) {
                            value = retrieveFromNode(key, node);
                            if (value != null) break;
                        }
                        
                        if (value != null) {
                            // Находим новые узлы для репликации
                            List<NodeInfo> candidateNodes = new ArrayList<>(healthyNodes);
                            candidateNodes.removeAll(currentNodes);
                            
                            // Сортируем по близости к ключу
                            KademliaId keyId = KademliaId.fromString(key);
                            candidateNodes.sort((n1, n2) -> {
                                KademliaId id1 = n1.getNodeId();
                                KademliaId id2 = n2.getNodeId();
                                return id1.distanceTo(keyId).compareTo(id2.distanceTo(keyId));
                            });
                            
                            // Добавляем реплики на новые узлы
                            int neededReplicas = config.getTargetReplicationFactor() - currentNodes.size();
                            for (int i = 0; i < Math.min(neededReplicas, candidateNodes.size()); i++) {
                                NodeInfo node = candidateNodes.get(i);
                                if (replicateToNode(key, value, node)) {
                                    // Обновляем карты
                                    currentNodes.add(node);
                                    nodeKeyMap.computeIfAbsent(node, k -> new HashSet<>()).add(key);
                                }
                            }
                        }
                    }
                }
                
                return true;
                
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<DataIntegrityReport> checkDataIntegrity() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Checking data integrity");
                
                long startTime = System.currentTimeMillis();
                
                long totalKeys = keyNodeMap.size();
                long keysWithSufficientReplicas = 0;
                long keysWithInsufficientReplicas = 0;
                long inconsistentKeys = 0;
                long orphanedKeys = 0;
                
                Map<String, Integer> replicationFactorDistribution = new HashMap<>();
                List<String> problematicKeys = new ArrayList<>();
                
                // Проверяем каждый ключ
                for (Map.Entry<String, Set<NodeInfo>> entry : keyNodeMap.entrySet()) {
                    String key = entry.getKey();
                    Set<NodeInfo> nodes = entry.getValue();
                    
                    // Удаляем отказавшие узлы
                    nodes.removeIf(node -> failureDetector.getFailedNodes().contains(node));
                    
                    int replicationFactor = nodes.size();
                    
                    // Обновляем распределение факторов репликации
                    replicationFactorDistribution.put(
                        String.valueOf(replicationFactor),
                        replicationFactorDistribution.getOrDefault(String.valueOf(replicationFactor), 0) + 1
                    );
                    
                    // Проверяем достаточность реплик
                    if (replicationFactor >= config.getTargetReplicationFactor()) {
                        keysWithSufficientReplicas++;
                    } else if (replicationFactor > 0) {
                        keysWithInsufficientReplicas++;
                        problematicKeys.add(key);
                    } else {
                        orphanedKeys++;
                        problematicKeys.add(key);
                    }
                    
                    // Проверяем консистентность (упрощенно)
                    // Для полной проверки нужно сравнивать значения на всех узлах
                }
                
                long checkTime = System.currentTimeMillis() - startTime;
                
                // Формируем итоговый отчет
                boolean integrityOk = keysWithInsufficientReplicas == 0 && inconsistentKeys == 0 && orphanedKeys == 0;
                
                String summary = String.format(
                    "Data integrity check completed in %dms. Found %d keys with %d sufficient, %d insufficient, " +
                    "%d inconsistent, %d orphaned.",
                    checkTime, totalKeys, keysWithSufficientReplicas, keysWithInsufficientReplicas,
                    inconsistentKeys, orphanedKeys
                );
                
                return new DataIntegrityReport(
                    integrityOk,
                    totalKeys,
                    keysWithSufficientReplicas,
                    keysWithInsufficientReplicas,
                    inconsistentKeys,
                    orphanedKeys,
                    replicationFactorDistribution,
                    problematicKeys,
                    checkTime,
                    summary
                );
                
            } catch (Exception e) {
                return new DataIntegrityReport(
                    false, 0, 0, 0, 0, 0, new HashMap<>(), new ArrayList<>(),
                    0, "Data integrity check failed: " + e.getMessage()
                );
            }
        }, executor);
    }
    
    @Override
    public RecoveryStats getRecoveryStats() {
        long recoveryCount = totalRecoveries.get();
        double avgRecoveryTime = recoveryCount > 0 ? 
            (double) totalRecoveryTime.get() / recoveryCount : 0.0;
        
        return new RecoveryStats(
            totalRecoveries.get(),
            successfulRecoveries.get(),
            failedRecoveries.get(),
            totalKeysRecovered.get(),
            totalKeysLost.get(),
            avgRecoveryTime,
            lastRecoveryTime,
            activeRecoveries.size()
        );
    }
    
    @Override
    public void configure(RecoveryConfig config) {
        this.config = config;
        
        // Перезапускаем задачу перебалансировки с новым интервалом
        if (running && config.isAutoRebalanceEnabled()) {
            if (rebalanceTask != null) {
                rebalanceTask.cancel(false);
            }
            
            rebalanceTask = scheduler.scheduleAtFixedRate(
                () -> rebalanceData(),
                config.getRebalanceIntervalMs(),
                config.getRebalanceIntervalMs(),
                TimeUnit.MILLISECONDS
            );
        }
    }
    
    // Реализация FailureListener
    
    @Override
    public void onNodeFailure(NodeInfo node, String reason) {
        // Автоматически обрабатываем отказ узла
        handleNodeFailure(node);
    }
    
    @Override
    public void onNodeRecovery(NodeInfo node) {
        // Автоматически обрабатываем восстановление узла
        handleNodeRecovery(node);
    }
    
    @Override
    public void onNetworkPartition(String partitionId, NodeInfo[] affectedNodes) {
        // Обработка разделения сети
        System.out.println("Network partition detected: " + partitionId + " affecting " + affectedNodes.length + " nodes");
    }
    
    @Override
    public void onPartitionHealed(String partitionId) {
        // Обработка восстановления сети
        System.out.println("Network partition healed: " + partitionId);
        
        // Запускаем перебалансировку данных
        rebalanceData();
    }
}

