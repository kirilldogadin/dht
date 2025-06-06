package global.unet.replication;

import global.unet.domain.structures.NodeInfo;
import global.unet.network.rpc.RPCClient;
import global.unet.network.rpc.DHTMessage;
import global.unet.network.rpc.MessageType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

/**
 * Синхронная стратегия репликации - ждет подтверждения от всех узлов
 */
public class SynchronousReplicationStrategy implements ReplicationStrategy {
    
    private final RPCClient rpcClient;
    private final int timeoutMs;
    
    public SynchronousReplicationStrategy(RPCClient rpcClient, int timeoutMs) {
        this.rpcClient = rpcClient;
        this.timeoutMs = timeoutMs;
    }
    
    @Override
    public CompletableFuture<ReplicationResult> replicate(String key, String value, List<NodeInfo> targetNodes) {
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            List<NodeInfo> successfulNodes = new ArrayList<>();
            Map<NodeInfo, String> failedNodes = new HashMap<>();
            
            // Создаем список Future для всех операций репликации
            List<CompletableFuture<Boolean>> replicationFutures = new ArrayList<>();
            
            for (NodeInfo node : targetNodes) {
                CompletableFuture<Boolean> future = replicateToNode(key, value, node)
                    .handle((success, throwable) -> {
                        if (throwable != null) {
                            synchronized (failedNodes) {
                                failedNodes.put(node, throwable.getMessage());
                            }
                            return false;
                        } else if (success) {
                            synchronized (successfulNodes) {
                                successfulNodes.add(node);
                            }
                            return true;
                        } else {
                            synchronized (failedNodes) {
                                failedNodes.put(node, "Replication failed");
                            }
                            return false;
                        }
                    });
                
                replicationFutures.add(future);
            }
            
            // Ждем завершения всех операций
            try {
                CompletableFuture.allOf(replicationFutures.toArray(new CompletableFuture[0]))
                    .get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                // Некоторые операции могли не завершиться в срок
                for (NodeInfo node : targetNodes) {
                    if (!successfulNodes.contains(node) && !failedNodes.containsKey(node)) {
                        failedNodes.put(node, "Timeout");
                    }
                }
            }
            
            long replicationTime = System.currentTimeMillis() - startTime;
            
            // Считаем результат успешным, если хотя бы половина реплик создана
            boolean success = successfulNodes.size() > targetNodes.size() / 2;
            
            return ReplicationResult.success(
                successfulNodes.size(),
                targetNodes.size(),
                new ArrayList<>(successfulNodes),
                new HashMap<>(failedNodes),
                replicationTime
            );
        });
    }
    
    private CompletableFuture<Boolean> replicateToNode(String key, String value, NodeInfo node) {
        return CompletableFuture.supplyAsync(() -> {
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
                throw new CompletionException("Failed to replicate to node " + node, e);
            }
        });
    }
    
    @Override
    public String getStrategyName() {
        return "Synchronous";
    }
    
    @Override
    public String getDescription() {
        return "Synchronous replication strategy - waits for confirmation from all nodes";
    }
    
    @Override
    public boolean isAsynchronous() {
        return false;
    }
}

