package global.unet.replication;

import global.unet.domain.id.KademliaId;
import global.unet.domain.structures.NodeInfo;
import global.unet.domain.routing.XorTreeRoutingTable;
import global.unet.network.rpc.RPCClient;
import global.unet.network.rpc.DHTMessage;
import global.unet.network.rpc.MessageType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Основная реализация менеджера репликации
 */
public class DHTReplicationManager implements ReplicationManager {
    
    private final KademliaId nodeId;
    private final XorTreeRoutingTable routingTable;
    private final RPCClient rpcClient;
    private final ReplicationStrategy synchronousStrategy;
    private final ReplicationStrategy asynchronousStrategy;
    
    // Статистика
    private final AtomicLong totalReplications = new AtomicLong(0);
    private final AtomicLong successfulReplications = new AtomicLong(0);
    private final AtomicLong failedReplications = new AtomicLong(0);
    private final Map<String, Set<NodeInfo>> keyReplicaMap = new ConcurrentHashMap<>();
    private volatile long lastSyncTime = System.currentTimeMillis();
    
    // Конфигурация
    private final int defaultReplicationFactor;
    private final boolean useAsynchronousReplication;
    
    public DHTReplicationManager(KademliaId nodeId, XorTreeRoutingTable routingTable, 
                               RPCClient rpcClient, int defaultReplicationFactor,
                               boolean useAsynchronousReplication) {
        this.nodeId = nodeId;
        this.routingTable = routingTable;
        this.rpcClient = rpcClient;
        this.defaultReplicationFactor = defaultReplicationFactor;
        this.useAsynchronousReplication = useAsynchronousReplication;
        
        // Инициализируем стратегии
        this.synchronousStrategy = new SynchronousReplicationStrategy(rpcClient, 5000);
        this.asynchronousStrategy = new AsynchronousReplicationStrategy(rpcClient, 
                                                                       Math.max(1, defaultReplicationFactor / 2));
    }
    
    @Override
    public CompletableFuture<Boolean> store(String key, String value, int replicationFactor) {
        totalReplications.incrementAndGet();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Получаем узлы для репликации
                List<NodeInfo> replicationNodes = getReplicationNodes(key, replicationFactor);
                
                if (replicationNodes.isEmpty()) {
                    failedReplications.incrementAndGet();
                    return false;
                }
                
                // Выбираем стратегию репликации
                ReplicationStrategy strategy = useAsynchronousReplication ? 
                    asynchronousStrategy : synchronousStrategy;
                
                // Выполняем репликацию
                ReplicationResult result = strategy.replicate(key, value, replicationNodes).join();
                
                if (result.isSuccess()) {
                    // Обновляем карту реплик
                    keyReplicaMap.put(key, new HashSet<>(result.getSuccessfulNodes()));
                    successfulReplications.incrementAndGet();
                    return true;
                } else {
                    failedReplications.incrementAndGet();
                    return false;
                }
                
            } catch (Exception e) {
                failedReplications.incrementAndGet();
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<String> retrieve(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Получаем узлы, которые могут содержать данные
                List<NodeInfo> possibleNodes = getReplicationNodes(key, defaultReplicationFactor);
                
                // Пробуем получить данные с каждого узла
                for (NodeInfo node : possibleNodes) {
                    try {
                        String value = retrieveFromNode(key, node);
                        if (value != null) {
                            return value;
                        }
                    } catch (Exception e) {
                        // Продолжаем поиск на других узлах
                    }
                }
                
                return null; // Данные не найдены
                
            } catch (Exception e) {
                return null;
            }
        });
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
    
    @Override
    public CompletableFuture<Boolean> delete(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Получаем узлы с репликами
                Set<NodeInfo> replicaNodes = keyReplicaMap.get(key);
                if (replicaNodes == null) {
                    replicaNodes = new HashSet<>(getReplicationNodes(key, defaultReplicationFactor));
                }
                
                int successfulDeletes = 0;
                
                // Удаляем с каждого узла
                for (NodeInfo node : replicaNodes) {
                    try {
                        if (deleteFromNode(key, node)) {
                            successfulDeletes++;
                        }
                    } catch (Exception e) {
                        // Продолжаем удаление с других узлов
                    }
                }
                
                // Удаляем из карты реплик
                keyReplicaMap.remove(key);
                
                // Считаем успешным, если удалили хотя бы с половины узлов
                return successfulDeletes > replicaNodes.size() / 2;
                
            } catch (Exception e) {
                return false;
            }
        });
    }
    
    private boolean deleteFromNode(String key, NodeInfo node) {
        // Здесь должна быть реализация DELETE операции
        // Пока возвращаем true как заглушку
        return true;
    }
    
    @Override
    public List<NodeInfo> getReplicationNodes(String key, int replicationFactor) {
        // Вычисляем ID ключа
        KademliaId keyId = KademliaId.fromString(key);
        
        // Получаем ближайшие узлы из таблицы маршрутизации
        List<NodeInfo> closestNodes = routingTable.findClosestNodes(keyId, replicationFactor * 2);
        
        // Возвращаем первые replicationFactor узлов
        return closestNodes.subList(0, Math.min(replicationFactor, closestNodes.size()));
    }
    
    @Override
    public CompletableFuture<Boolean> synchronizeReplicas(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Получаем текущие реплики
                Set<NodeInfo> currentReplicas = keyReplicaMap.get(key);
                if (currentReplicas == null) {
                    return false;
                }
                
                // Получаем значение с одной из реплик
                String value = null;
                for (NodeInfo node : currentReplicas) {
                    value = retrieveFromNode(key, node);
                    if (value != null) break;
                }
                
                if (value == null) {
                    return false;
                }
                
                // Синхронизируем все реплики
                int successfulSyncs = 0;
                for (NodeInfo node : currentReplicas) {
                    try {
                        if (replicateToNodeSync(key, value, node)) {
                            successfulSyncs++;
                        }
                    } catch (Exception e) {
                        // Продолжаем синхронизацию других реплик
                    }
                }
                
                lastSyncTime = System.currentTimeMillis();
                return successfulSyncs > currentReplicas.size() / 2;
                
            } catch (Exception e) {
                return false;
            }
        });
    }
    
    private boolean replicateToNodeSync(String key, String value, NodeInfo node) {
        try {
            DHTMessage storeMessage = new DHTMessage(
                MessageType.STORE,
                UUID.randomUUID().toString(),
                key,
                value,
                null,
                System.currentTimeMillis()
            );
            
            DHTMessage response = rpcClient.sendMessage(storeMessage, node.getAddress(), node.getPort());
            return response != null && response.getType() == MessageType.STORE_RESPONSE;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public CompletableFuture<Boolean> checkConsistency(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Set<NodeInfo> replicaNodes = keyReplicaMap.get(key);
                if (replicaNodes == null || replicaNodes.size() < 2) {
                    return true; // Нет реплик для проверки
                }
                
                // Получаем значения со всех реплик
                Map<String, Integer> valueCount = new HashMap<>();
                
                for (NodeInfo node : replicaNodes) {
                    try {
                        String value = retrieveFromNode(key, node);
                        if (value != null) {
                            valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
                        }
                    } catch (Exception e) {
                        // Игнорируем недоступные узлы
                    }
                }
                
                // Проверяем, что все значения одинаковые
                return valueCount.size() <= 1;
                
            } catch (Exception e) {
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> repairReplicas(String key, int targetReplicationFactor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Set<NodeInfo> currentReplicas = keyReplicaMap.get(key);
                if (currentReplicas == null) {
                    currentReplicas = new HashSet<>();
                }
                
                // Если реплик достаточно, ничего не делаем
                if (currentReplicas.size() >= targetReplicationFactor) {
                    return true;
                }
                
                // Получаем значение с одной из существующих реплик
                String value = null;
                for (NodeInfo node : currentReplicas) {
                    value = retrieveFromNode(key, node);
                    if (value != null) break;
                }
                
                if (value == null) {
                    return false; // Нет доступных реплик
                }
                
                // Получаем дополнительные узлы для репликации
                List<NodeInfo> allPossibleNodes = getReplicationNodes(key, targetReplicationFactor * 2);
                List<NodeInfo> newNodes = new ArrayList<>();
                
                for (NodeInfo node : allPossibleNodes) {
                    if (!currentReplicas.contains(node) && newNodes.size() < targetReplicationFactor - currentReplicas.size()) {
                        newNodes.add(node);
                    }
                }
                
                // Создаем недостающие реплики
                ReplicationStrategy strategy = synchronousStrategy;
                ReplicationResult result = strategy.replicate(key, value, newNodes).join();
                
                if (result.isSuccess()) {
                    // Обновляем карту реплик
                    currentReplicas.addAll(result.getSuccessfulNodes());
                    keyReplicaMap.put(key, currentReplicas);
                    return true;
                }
                
                return false;
                
            } catch (Exception e) {
                return false;
            }
        });
    }
    
    @Override
    public ReplicationStats getReplicationStats() {
        long totalKeys = keyReplicaMap.size();
        long totalReplicas = keyReplicaMap.values().stream()
            .mapToLong(Set::size)
            .sum();
        
        double averageReplicationFactor = totalKeys > 0 ? 
            (double) totalReplicas / totalKeys : 0.0;
        
        // Подсчитываем несогласованные реплики (упрощенно)
        long inconsistentReplicas = 0; // Требует дополнительной проверки
        
        return new ReplicationStats(
            totalKeys,
            totalReplicas,
            averageReplicationFactor,
            inconsistentReplicas,
            failedReplications.get(),
            successfulReplications.get(),
            lastSyncTime
        );
    }
}

