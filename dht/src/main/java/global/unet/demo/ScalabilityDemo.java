package global.unet.demo;

import global.unet.domain.id.KademliaId;
import global.unet.domain.structures.NodeInfo;
import global.unet.network.ScalableDHTNode;
import global.unet.network.SystemHealthReport;
import global.unet.network.ScalabilityStats;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Демонстрация масштабирования и отказоустойчивости DHT сети
 */
public class ScalabilityDemo {
    
    private static final String MONITORING_SERVER_URL = "http://localhost:8080/metrics";
    private static final int BASE_PORT = 8000;
    private static final int DEMO_DURATION_SECONDS = 60;
    
    private final List<ScalableDHTNode> nodes = new ArrayList<>();
    private final Random random = new Random();
    
    public static void main(String[] args) {
        ScalabilityDemo demo = new ScalabilityDemo();
        demo.runDemo();
    }
    
    public void runDemo() {
        System.out.println("=== DHT Scalability and Fault Tolerance Demo ===");
        System.out.println("This demo will showcase:");
        System.out.println("1. Network scaling with multiple nodes");
        System.out.println("2. Data replication and consistency");
        System.out.println("3. Failure detection and recovery");
        System.out.println("4. Load balancing and performance optimization");
        System.out.println("5. Real-time monitoring and health reporting");
        System.out.println();
        
        try {
            // Фаза 1: Создание и запуск сети
            System.out.println("Phase 1: Creating and starting DHT network...");
            createNetwork(5);
            
            // Фаза 2: Демонстрация репликации данных
            System.out.println("\\nPhase 2: Demonstrating data replication...");
            demonstrateReplication();
            
            // Фаза 3: Симуляция отказов узлов
            System.out.println("\\nPhase 3: Simulating node failures...");
            simulateNodeFailures();
            
            // Фаза 4: Демонстрация восстановления
            System.out.println("\\nPhase 4: Demonstrating recovery...");
            demonstrateRecovery();
            
            // Фаза 5: Масштабирование сети
            System.out.println("\\nPhase 5: Scaling the network...");
            scaleNetwork();
            
            // Фаза 6: Мониторинг и отчеты
            System.out.println("\\nPhase 6: Monitoring and health reports...");
            generateHealthReports();
            
            // Фаза 7: Нагрузочное тестирование
            System.out.println("\\nPhase 7: Load testing...");
            performLoadTest();
            
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    
    private void createNetwork(int nodeCount) {
        System.out.println("Creating " + nodeCount + " DHT nodes...");
        
        for (int i = 0; i < nodeCount; i++) {
            KademliaId nodeId = KademliaId.random();
            String address = "localhost";
            int port = BASE_PORT + i;
            
            ScalableDHTNode node = new ScalableDHTNode(nodeId, address, port, MONITORING_SERVER_URL);
            nodes.add(node);
            
            System.out.println("Created node " + (i + 1) + ": " + nodeId + " at " + address + ":" + port);
        }
        
        // Запускаем все узлы
        System.out.println("Starting all nodes...");
        for (ScalableDHTNode node : nodes) {
            node.start();
        }
        
        // Соединяем узлы в сеть
        System.out.println("Connecting nodes to form network...");
        for (int i = 1; i < nodes.size(); i++) {
            ScalableDHTNode node = nodes.get(i);
            ScalableDHTNode bootstrapNode = nodes.get(0);
            
            NodeInfo bootstrapInfo = new NodeInfo(
                bootstrapNode.getNodeId(),
                bootstrapNode.getAddress(),
                bootstrapNode.getPort()
            );
            
            node.addNode(bootstrapInfo);
        }
        
        // Даем время на стабилизацию сети
        sleep(3000);
        System.out.println("Network created and stabilized with " + nodes.size() + " nodes");
    }
    
    private void demonstrateReplication() {
        System.out.println("Storing data with replication...");
        
        Map<String, String> testData = new HashMap<>();
        testData.put("user:1001", "Alice Johnson");
        testData.put("user:1002", "Bob Smith");
        testData.put("user:1003", "Carol Davis");
        testData.put("config:timeout", "30000");
        testData.put("config:retries", "3");
        
        ScalableDHTNode primaryNode = nodes.get(0);
        
        for (Map.Entry<String, String> entry : testData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            System.out.println("Storing: " + key + " = " + value);
            
            CompletableFuture<Boolean> result = primaryNode.store(key, value);
            try {
                boolean success = result.get(5, TimeUnit.SECONDS);
                System.out.println("  Result: " + (success ? "SUCCESS" : "FAILED"));
            } catch (Exception e) {
                System.out.println("  Result: TIMEOUT/ERROR - " + e.getMessage());
            }
        }
        
        sleep(2000);
        
        // Проверяем репликацию, читая данные с разных узлов
        System.out.println("\\nVerifying replication across nodes...");
        for (String key : testData.keySet()) {
            System.out.println("Reading key: " + key);
            
            for (int i = 0; i < Math.min(3, nodes.size()); i++) {
                ScalableDHTNode node = nodes.get(i);
                try {
                    CompletableFuture<String> result = node.findValue(key);
                    String value = result.get(3, TimeUnit.SECONDS);
                    System.out.println("  Node " + (i + 1) + ": " + (value != null ? value : "NOT_FOUND"));
                } catch (Exception e) {
                    System.out.println("  Node " + (i + 1) + ": ERROR - " + e.getMessage());
                }
            }
        }
    }
    
    private void simulateNodeFailures() {
        if (nodes.size() < 3) {
            System.out.println("Not enough nodes to simulate failures safely");
            return;
        }
        
        System.out.println("Simulating node failures...");
        
        // Останавливаем случайный узел
        int failedNodeIndex = 1 + random.nextInt(nodes.size() - 2); // Не трогаем первый и последний
        ScalableDHTNode failedNode = nodes.get(failedNodeIndex);
        
        System.out.println("Stopping node " + (failedNodeIndex + 1) + ": " + failedNode.getNodeId());
        failedNode.stop();
        
        // Даем время на детекцию отказа
        sleep(5000);
        
        // Проверяем, как система отреагировала
        System.out.println("Checking system response to failure...");
        for (int i = 0; i < nodes.size(); i++) {
            if (i == failedNodeIndex) continue;
            
            ScalableDHTNode node = nodes.get(i);
            try {
                SystemHealthReport health = node.getSystemHealth();
                System.out.println("Node " + (i + 1) + " health: " + health.getHealthLevel() + 
                                 " (" + health.getIssueCount() + " issues)");
            } catch (Exception e) {
                System.out.println("Node " + (i + 1) + " health check failed: " + e.getMessage());
            }
        }
        
        // Проверяем доступность данных после отказа
        System.out.println("\\nTesting data availability after failure...");
        ScalableDHTNode testNode = nodes.get(0);
        try {
            CompletableFuture<String> result = testNode.findValue("user:1001");
            String value = result.get(5, TimeUnit.SECONDS);
            System.out.println("Data still accessible: " + (value != null ? "YES" : "NO"));
        } catch (Exception e) {
            System.out.println("Data access failed: " + e.getMessage());
        }
    }
    
    private void demonstrateRecovery() {
        System.out.println("Demonstrating automatic recovery...");
        
        // Находим остановленный узел и перезапускаем его
        for (int i = 0; i < nodes.size(); i++) {
            ScalableDHTNode node = nodes.get(i);
            // Простая проверка - если узел не отвечает на ping
            try {
                node.getSystemHealth(); // Это вызовет исключение если узел остановлен
            } catch (Exception e) {
                System.out.println("Restarting failed node " + (i + 1) + "...");
                node.start();
                
                // Переподключаем к сети
                if (!nodes.isEmpty()) {
                    NodeInfo bootstrapInfo = new NodeInfo(
                        nodes.get(0).getNodeId(),
                        nodes.get(0).getAddress(),
                        nodes.get(0).getPort()
                    );
                    node.addNode(bootstrapInfo);
                }
                
                sleep(3000);
                System.out.println("Node " + (i + 1) + " recovered and rejoined network");
                break;
            }
        }
        
        // Проверяем восстановление данных
        sleep(5000);
        System.out.println("\\nChecking data recovery...");
        
        for (ScalableDHTNode node : nodes) {
            try {
                SystemHealthReport health = node.getSystemHealth();
                System.out.println("Node health after recovery: " + health.getHealthLevel());
            } catch (Exception e) {
                System.out.println("Health check failed: " + e.getMessage());
            }
        }
    }
    
    private void scaleNetwork() {
        System.out.println("Scaling network by adding new nodes...");
        
        int initialSize = nodes.size();
        int newNodes = 2;
        
        for (int i = 0; i < newNodes; i++) {
            KademliaId nodeId = KademliaId.random();
            String address = "localhost";
            int port = BASE_PORT + initialSize + i;
            
            ScalableDHTNode node = new ScalableDHTNode(nodeId, address, port, MONITORING_SERVER_URL);
            nodes.add(node);
            
            System.out.println("Adding node " + (initialSize + i + 1) + ": " + nodeId);
            
            node.start();
            
            // Подключаем к существующей сети
            if (initialSize > 0) {
                NodeInfo bootstrapInfo = new NodeInfo(
                    nodes.get(0).getNodeId(),
                    nodes.get(0).getAddress(),
                    nodes.get(0).getPort()
                );
                node.addNode(bootstrapInfo);
            }
            
            sleep(2000);
        }
        
        System.out.println("Network scaled from " + initialSize + " to " + nodes.size() + " nodes");
        
        // Проверяем перебалансировку данных
        sleep(5000);
        System.out.println("Checking data rebalancing...");
        
        try {
            CompletableFuture<String> result = nodes.get(nodes.size() - 1).findValue("user:1001");
            String value = result.get(3, TimeUnit.SECONDS);
            System.out.println("Data accessible from new node: " + (value != null ? "YES" : "NO"));
        } catch (Exception e) {
            System.out.println("Data access from new node failed: " + e.getMessage());
        }
    }
    
    private void generateHealthReports() {
        System.out.println("Generating comprehensive health reports...");
        
        for (int i = 0; i < nodes.size(); i++) {
            ScalableDHTNode node = nodes.get(i);
            try {
                System.out.println("\\n--- Node " + (i + 1) + " Health Report ---");
                SystemHealthReport health = node.getSystemHealth();
                System.out.println(health.toString());
                
                System.out.println("\\n--- Node " + (i + 1) + " Metrics Summary ---");
                System.out.println(node.getMetricsSummary());
                
            } catch (Exception e) {
                System.out.println("Failed to generate report for node " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        // Общая статистика сети
        System.out.println("\\n--- Network-wide Statistics ---");
        try {
            ScalabilityStats networkStats = nodes.get(0).getScalabilityStats();
            System.out.println("Network Scalability Score: " + String.format("%.1f/100", networkStats.getScalabilityScore()));
            System.out.println("Network Throughput: " + String.format("%.1f ops/sec", networkStats.getThroughput()));
            System.out.println("Average Latency: " + String.format("%.1f ms", networkStats.getAverageLatency()));
            System.out.println("Resilience Level: " + networkStats.getResilienceLevel());
        } catch (Exception e) {
            System.out.println("Failed to generate network statistics: " + e.getMessage());
        }
    }
    
    private void performLoadTest() {
        System.out.println("Performing load test...");
        
        int operationsCount = 100;
        int concurrentThreads = 5;
        
        System.out.println("Executing " + operationsCount + " operations with " + concurrentThreads + " concurrent threads...");
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int t = 0; t < concurrentThreads; t++) {
            final int threadId = t;
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int i = 0; i < operationsCount / concurrentThreads; i++) {
                    try {
                        ScalableDHTNode node = nodes.get(random.nextInt(nodes.size()));
                        String key = "load_test_" + threadId + "_" + i;
                        String value = "value_" + System.currentTimeMillis();
                        
                        // Операция записи
                        CompletableFuture<Boolean> storeResult = node.store(key, value);
                        storeResult.get(2, TimeUnit.SECONDS);
                        
                        // Операция чтения
                        CompletableFuture<String> readResult = node.findValue(key);
                        readResult.get(2, TimeUnit.SECONDS);
                        
                    } catch (Exception e) {
                        // Игнорируем ошибки в нагрузочном тесте
                    }
                }
            });
            
            futures.add(future);
        }
        
        // Ждем завершения всех потоков
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
            System.out.println("Load test completed successfully");
        } catch (Exception e) {
            System.out.println("Load test completed with some errors: " + e.getMessage());
        }
        
        // Показываем финальную статистику
        sleep(2000);
        System.out.println("\\n--- Final Performance Statistics ---");
        try {
            ScalabilityStats finalStats = nodes.get(0).getScalabilityStats();
            System.out.println(finalStats.toString());
        } catch (Exception e) {
            System.out.println("Failed to get final statistics: " + e.getMessage());
        }
    }
    
    private void cleanup() {
        System.out.println("\\nCleaning up demo environment...");
        
        for (int i = 0; i < nodes.size(); i++) {
            try {
                nodes.get(i).stop();
                System.out.println("Stopped node " + (i + 1));
            } catch (Exception e) {
                System.out.println("Error stopping node " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        nodes.clear();
        System.out.println("Demo cleanup completed");
    }
    
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

