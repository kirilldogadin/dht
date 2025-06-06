package global.unet.scalability;

import global.unet.domain.id.KademliaId;
import global.unet.domain.structures.NodeInfo;
import global.unet.replication.*;
import global.unet.network.rpc.RPCClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Тесты для системы репликации данных
 */
public class ReplicationTest {
    
    public static void main(String[] args) {
        ReplicationTest test = new ReplicationTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        System.out.println("=== Replication System Tests ===");
        
        testReplicationManagerCreation();
        testSynchronousReplication();
        testAsynchronousReplication();
        testReplicationStats();
        
        System.out.println("All replication tests completed!");
    }
    
    private void testReplicationManagerCreation() {
        System.out.println("\\nTest: ReplicationManager Creation");
        
        try {
            KademliaId nodeId = KademliaId.random();
            MockRPCClient rpcClient = new MockRPCClient();
            
            DHTReplicationManager manager = new DHTReplicationManager(nodeId, null, rpcClient);
            manager.start();
            
            ReplicationStats stats = manager.getStats();
            assert stats != null : "Stats should not be null";
            assert stats.getTotalReplications() == 0 : "Initial replications should be 0";
            
            manager.stop();
            
            System.out.println("✓ ReplicationManager created and started successfully");
            
        } catch (Exception e) {
            System.out.println("✗ ReplicationManager creation failed: " + e.getMessage());
        }
    }
    
    private void testSynchronousReplication() {
        System.out.println("\\nTest: Synchronous Replication Strategy");
        
        try {
            MockRPCClient rpcClient = new MockRPCClient();
            SynchronousReplicationStrategy strategy = new SynchronousReplicationStrategy(rpcClient);
            
            List<NodeInfo> nodes = createTestNodes(3);
            
            CompletableFuture<ReplicationResult> future = strategy.replicate("test_key", "test_value", nodes);
            ReplicationResult result = future.get();
            
            assert result != null : "Result should not be null";
            assert result.isSuccess() : "Synchronous replication should succeed";
            assert result.getSuccessfulNodes() == 3 : "All nodes should succeed";
            
            System.out.println("✓ Synchronous replication completed successfully");
            System.out.println("  - Successful nodes: " + result.getSuccessfulNodes());
            System.out.println("  - Failed nodes: " + result.getFailedNodes());
            System.out.println("  - Replication time: " + result.getReplicationTimeMs() + "ms");
            
        } catch (Exception e) {
            System.out.println("✗ Synchronous replication test failed: " + e.getMessage());
        }
    }
    
    private void testAsynchronousReplication() {
        System.out.println("\\nTest: Asynchronous Replication Strategy");
        
        try {
            MockRPCClient rpcClient = new MockRPCClient();
            AsynchronousReplicationStrategy strategy = new AsynchronousReplicationStrategy(rpcClient);
            
            List<NodeInfo> nodes = createTestNodes(3);
            
            CompletableFuture<ReplicationResult> future = strategy.replicate("test_key", "test_value", nodes);
            ReplicationResult result = future.get();
            
            assert result != null : "Result should not be null";
            assert result.isSuccess() : "Asynchronous replication should succeed";
            
            System.out.println("✓ Asynchronous replication completed successfully");
            System.out.println("  - Successful nodes: " + result.getSuccessfulNodes());
            System.out.println("  - Failed nodes: " + result.getFailedNodes());
            System.out.println("  - Replication time: " + result.getReplicationTimeMs() + "ms");
            
        } catch (Exception e) {
            System.out.println("✗ Asynchronous replication test failed: " + e.getMessage());
        }
    }
    
    private void testReplicationStats() {
        System.out.println("\\nTest: Replication Statistics");
        
        try {
            KademliaId nodeId = KademliaId.random();
            MockRPCClient rpcClient = new MockRPCClient();
            
            DHTReplicationManager manager = new DHTReplicationManager(nodeId, null, rpcClient);
            manager.start();
            
            // Выполняем несколько операций репликации
            List<NodeInfo> nodes = createTestNodes(3);
            
            for (int i = 0; i < 5; i++) {
                CompletableFuture<ReplicationResult> future = manager.replicate("key_" + i, "value_" + i, nodes);
                future.get();
            }
            
            ReplicationStats stats = manager.getStats();
            
            assert stats.getTotalReplications() == 5 : "Should have 5 total replications";
            assert stats.getSuccessfulReplications() == 5 : "All replications should succeed";
            assert stats.getSuccessRate() == 100.0 : "Success rate should be 100%";
            
            System.out.println("✓ Replication statistics working correctly");
            System.out.println("  - Total replications: " + stats.getTotalReplications());
            System.out.println("  - Success rate: " + String.format("%.1f%%", stats.getSuccessRate()));
            System.out.println("  - Average time: " + String.format("%.1fms", stats.getAverageReplicationTime()));
            
            manager.stop();
            
        } catch (Exception e) {
            System.out.println("✗ Replication statistics test failed: " + e.getMessage());
        }
    }
    
    private List<NodeInfo> createTestNodes(int count) {
        List<NodeInfo> nodes = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            NodeInfo node = new NodeInfo(
                KademliaId.random(),
                "localhost",
                8000 + i
            );
            nodes.add(node);
        }
        
        return nodes;
    }
    
    /**
     * Mock RPC Client для тестирования
     */
    private static class MockRPCClient extends RPCClient {
        
        public MockRPCClient() {
            super(null, 0);
        }
        
        @Override
        public global.unet.network.rpc.DHTMessage sendMessage(
            global.unet.network.rpc.DHTMessage message, 
            String address, 
            int port) {
            
            // Симулируем успешный ответ
            return new global.unet.network.rpc.DHTMessage(
                global.unet.network.rpc.MessageType.STORE_RESPONSE,
                message.getMessageId(),
                message.getKey(),
                "OK",
                null,
                System.currentTimeMillis()
            );
        }
    }
}

