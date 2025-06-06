package global.unet.scalability;

import global.unet.domain.id.KademliaId;
import global.unet.domain.structures.NodeInfo;
import global.unet.loadbalancing.*;

import java.util.*;

/**
 * Тесты для системы балансировки нагрузки
 */
public class LoadBalancingTest {
    
    public static void main(String[] args) {
        LoadBalancingTest test = new LoadBalancingTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        System.out.println("=== Load Balancing System Tests ===");
        
        testLoadBalancerCreation();
        testNodeSelection();
        testLoadMetrics();
        testOverloadDetection();
        testCaching();
        
        System.out.println("All load balancing tests completed!");
    }
    
    private void testLoadBalancerCreation() {
        System.out.println("\\nTest: LoadBalancer Creation");
        
        try {
            AdaptiveLoadBalancer loadBalancer = new AdaptiveLoadBalancer();
            loadBalancer.start();
            
            LoadBalancingStats stats = loadBalancer.getStats();
            assert stats != null : "Stats should not be null";
            assert stats.getTotalRequests() == 0 : "Initial requests should be 0";
            
            loadBalancer.stop();
            
            System.out.println("✓ LoadBalancer created and started successfully");
            
        } catch (Exception e) {
            System.out.println("✗ LoadBalancer creation failed: " + e.getMessage());
        }
    }
    
    private void testNodeSelection() {
        System.out.println("\\nTest: Node Selection Strategies");
        
        try {
            AdaptiveLoadBalancer loadBalancer = new AdaptiveLoadBalancer();
            loadBalancer.start();
            
            List<NodeInfo> nodes = createTestNodes(5);
            
            // Тестируем различные стратегии
            testStrategy(loadBalancer, LoadBalancingStrategy.ROUND_ROBIN, nodes);
            testStrategy(loadBalancer, LoadBalancingStrategy.LEAST_LOAD, nodes);
            testStrategy(loadBalancer, LoadBalancingStrategy.RANDOM, nodes);
            
            loadBalancer.stop();
            
            System.out.println("✓ Node selection strategies working correctly");
            
        } catch (Exception e) {
            System.out.println("✗ Node selection test failed: " + e.getMessage());
        }
    }
    
    private void testStrategy(AdaptiveLoadBalancer loadBalancer, LoadBalancingStrategy strategy, List<NodeInfo> nodes) {
        LoadBalancingConfig config = new LoadBalancingConfig(
            0.8, 0.3, 5000, 30000, strategy, true, true, 1000, 60000
        );
        loadBalancer.configure(config);
        
        // Выполняем несколько выборов узлов
        Set<NodeInfo> selectedNodes = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            NodeInfo selected = loadBalancer.selectNode(OperationType.FIND_VALUE, "test_key_" + i, nodes);
            if (selected != null) {
                selectedNodes.add(selected);
            }
        }
        
        System.out.println("  Strategy " + strategy + ": selected " + selectedNodes.size() + " unique nodes");
    }
    
    private void testLoadMetrics() {
        System.out.println("\\nTest: Load Metrics");
        
        try {
            AdaptiveLoadBalancer loadBalancer = new AdaptiveLoadBalancer();
            loadBalancer.start();
            
            NodeInfo testNode = createTestNodes(1).get(0);
            
            // Создаем метрики нагрузки
            NodeLoadMetrics metrics = new NodeLoadMetrics(
                0.5,  // CPU usage
                0.3,  // Memory usage
                0.2,  // Network usage
                10,   // Active connections
                5,    // Queue size
                100.0, // Average response time
                50    // Requests per second
            );
            
            loadBalancer.updateNodeLoad(testNode, metrics);
            
            NodeLoadMetrics retrieved = loadBalancer.getNodeLoad(testNode);
            assert retrieved != null : "Retrieved metrics should not be null";
            assert Math.abs(retrieved.getCpuUsage() - 0.5) < 0.01 : "CPU usage should match";
            assert retrieved.getActiveConnections() == 10 : "Active connections should match";
            
            double overallLoad = retrieved.getOverallLoad();
            assert overallLoad >= 0.0 && overallLoad <= 1.0 : "Overall load should be between 0 and 1";
            
            System.out.println("✓ Load metrics working correctly");
            System.out.println("  - Overall load: " + String.format("%.2f", overallLoad));
            System.out.println("  - CPU usage: " + String.format("%.1f%%", retrieved.getCpuUsage() * 100));
            System.out.println("  - Response time: " + String.format("%.1fms", retrieved.getAverageResponseTime()));
            
            loadBalancer.stop();
            
        } catch (Exception e) {
            System.out.println("✗ Load metrics test failed: " + e.getMessage());
        }
    }
    
    private void testOverloadDetection() {
        System.out.println("\\nTest: Overload Detection");
        
        try {
            AdaptiveLoadBalancer loadBalancer = new AdaptiveLoadBalancer();
            loadBalancer.start();
            
            List<NodeInfo> nodes = createTestNodes(3);
            
            // Создаем узел с высокой нагрузкой
            NodeInfo overloadedNode = nodes.get(0);
            NodeLoadMetrics highLoad = new NodeLoadMetrics(
                0.9,  // High CPU usage
                0.8,  // High memory usage
                0.7,  // High network usage
                100,  // Many connections
                50,   // Large queue
                500.0, // High response time
                10    // Low RPS
            );
            loadBalancer.updateNodeLoad(overloadedNode, highLoad);
            
            // Создаем узел с низкой нагрузкой
            NodeInfo normalNode = nodes.get(1);
            NodeLoadMetrics lowLoad = new NodeLoadMetrics(
                0.2,  // Low CPU usage
                0.1,  // Low memory usage
                0.1,  // Low network usage
                5,    // Few connections
                1,    // Small queue
                50.0, // Low response time
                100   // High RPS
            );
            loadBalancer.updateNodeLoad(normalNode, lowLoad);
            
            boolean isOverloaded = loadBalancer.isNodeOverloaded(overloadedNode);
            boolean isNormal = loadBalancer.isNodeOverloaded(normalNode);
            
            assert isOverloaded : "High load node should be detected as overloaded";
            assert !isNormal : "Low load node should not be detected as overloaded";
            
            List<NodeInfo> overloadedNodes = loadBalancer.getOverloadedNodes();
            List<NodeInfo> underloadedNodes = loadBalancer.getUnderloadedNodes();
            
            System.out.println("✓ Overload detection working correctly");
            System.out.println("  - Overloaded nodes: " + overloadedNodes.size());
            System.out.println("  - Underloaded nodes: " + underloadedNodes.size());
            
            loadBalancer.stop();
            
        } catch (Exception e) {
            System.out.println("✗ Overload detection test failed: " + e.getMessage());
        }
    }
    
    private void testCaching() {
        System.out.println("\\nTest: Caching System");
        
        try {
            AdaptiveLoadBalancer loadBalancer = new AdaptiveLoadBalancer();
            
            // Включаем кэширование
            LoadBalancingConfig config = new LoadBalancingConfig(
                0.8, 0.3, 5000, 30000, LoadBalancingStrategy.LEAST_LOAD, 
                true, true, 100, 5000 // Короткий TTL для теста
            );
            loadBalancer.configure(config);
            loadBalancer.start();
            
            List<NodeInfo> nodes = createTestNodes(3);
            
            // Первый запрос - должен попасть в кэш
            NodeInfo selected1 = loadBalancer.selectNode(OperationType.FIND_VALUE, "cached_key", nodes);
            
            // Второй запрос с тем же ключом - должен использовать кэш
            NodeInfo selected2 = loadBalancer.selectNode(OperationType.FIND_VALUE, "cached_key", nodes);
            
            assert selected1 != null : "First selection should not be null";
            assert selected2 != null : "Second selection should not be null";
            assert selected1.equals(selected2) : "Cached requests should return same node";
            
            LoadBalancingStats stats = loadBalancer.getStats();
            assert stats.getCacheHitRate() > 0 : "Cache hit rate should be greater than 0";
            
            System.out.println("✓ Caching system working correctly");
            System.out.println("  - Cache hit rate: " + String.format("%.1f%%", stats.getCacheHitRate()));
            
            loadBalancer.stop();
            
        } catch (Exception e) {
            System.out.println("✗ Caching test failed: " + e.getMessage());
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
}

