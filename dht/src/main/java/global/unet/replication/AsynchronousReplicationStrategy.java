package global.unet.replication;

import global.unet.domain.structures.NodeInfo;
import global.unet.network.rpc.RPCClient;
import global.unet.network.rpc.DHTMessage;
import global.unet.network.rpc.MessageType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Асинхронная стратегия репликации - не ждет подтверждения от всех узлов
 */
public class AsynchronousReplicationStrategy implements ReplicationStrategy {
    
    private final RPCClient rpcClient;
    private final ExecutorService executorService;
    private final int minSuccessfulReplicas;
    
    public AsynchronousReplicationStrategy(RPCClient rpcClient, int minSuccessfulReplicas) {
        this.rpcClient = rpcClient;
        this.minSuccessfulReplicas = minSuccessfulReplicas;
        this.executorService = Executors.newCachedThreadPool();
    }
    
    @Override
    public CompletableFuture<ReplicationResult> replicate(String key, String value, List<NodeInfo> targetNodes) {
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            List<NodeInfo> successfulNodes = Collections.synchronizedList(new ArrayList<>());
            Map<NodeInfo, String> failedNodes = Collections.synchronizedMap(new HashMap<>());
            
            // Запускаем репликацию на все узлы асинхронно
            List<CompletableFuture<Void>> replicationFutures = new ArrayList<>();
            
            for (NodeInfo node : targetNodes) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        boolean success = replicateToNodeSync(key, value, node);
                        if (success) {
                            successfulNodes.add(node);
                        } else {
                            failedNodes.put(node, "Replication failed");
                        }
                    } catch (Exception e) {
                        failedNodes.put(node, e.getMessage());
                    }
                }, executorService);
                
                replicationFutures.add(future);
            }
            
            // Ждем минимального количества успешных реплик
            int successCount = 0;
            long checkStartTime = System.currentTimeMillis();
            long maxWaitTime = 5000; // 5 секунд максимум
            
            while (successCount < minSuccessfulReplicas && 
                   (System.currentTimeMillis() - checkStartTime) < maxWaitTime) {
                
                successCount = successfulNodes.size();
                
                if (successCount >= minSuccessfulReplicas) {
                    break;
                }
                
                try {
                    Thread.sleep(100); // Проверяем каждые 100мс
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            long replicationTime = System.currentTimeMillis() - startTime;
            
            // Результат успешен, если достигнуто минимальное количество реплик
            boolean success = successfulNodes.size() >= minSuccessfulReplicas;
            
            if (success) {
                return ReplicationResult.success(
                    successfulNodes.size(),
                    targetNodes.size(),
                    new ArrayList<>(successfulNodes),
                    new HashMap<>(failedNodes),
                    replicationTime
                );
            } else {
                return ReplicationResult.failure(
                    String.format("Only %d/%d replicas successful, minimum required: %d",
                                successfulNodes.size(), targetNodes.size(), minSuccessfulReplicas),
                    replicationTime
                );
            }
        });
    }
    
    private boolean replicateToNodeSync(String key, String value, NodeInfo node) {
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
            
            // Проверяем ответ
            return response != null && response.getType() == MessageType.STORE_RESPONSE;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to replicate to node " + node, e);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Asynchronous";
    }
    
    @Override
    public String getDescription() {
        return String.format("Asynchronous replication strategy - waits for minimum %d successful replicas", 
                           minSuccessfulReplicas);
    }
    
    @Override
    public boolean isAsynchronous() {
        return true;
    }
    
    /**
     * Закрывает executor service
     */
    public void shutdown() {
        executorService.shutdown();
    }
}

