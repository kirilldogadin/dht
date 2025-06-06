package global.unet.network;

import global.unet.domain.id.KademliaId;
import global.unet.domain.structures.NodeInfo;
import global.unet.domain.routing.XorTreeRoutingTable;
import global.unet.network.rpc.*;
import global.unet.replication.*;
import global.unet.failure.*;
import global.unet.loadbalancing.*;
import global.unet.monitoring.DHTMetricsAgent;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Расширенный DHT узел с поддержкой масштабирования и отказоустойчивости
 */
public class ScalableDHTNode extends SimpleDHTNode {
    
    // Новые компоненты
    private final ReplicationManager replicationManager;
    private final FailureDetector failureDetector;
    private final RecoveryManager recoveryManager;
    private final LoadBalancer loadBalancer;
    private final DHTMetricsAgent metricsAgent;
    
    // Конфигурация
    private final ScalabilityConfig config;
    
    public ScalableDHTNode(KademliaId nodeId, String address, int port, String monitoringServerUrl) {
        super(nodeId, address, port);
        
        this.config = new ScalabilityConfig();
        
        // Инициализируем компоненты
        this.replicationManager = new DHTReplicationManager(nodeId, getRoutingTable(), getRpcClient());
        this.failureDetector = new HeartbeatFailureDetector(getRpcClient());
        this.recoveryManager = new DHTRecoveryManager(nodeId, getRoutingTable(), getRpcClient(), 
                                                     replicationManager, failureDetector);
        this.loadBalancer = new AdaptiveLoadBalancer();
        this.metricsAgent = new DHTMetricsAgent(nodeId.toString(), monitoringServerUrl);
        
        // Настраиваем интеграцию
        setupIntegration();
    }
    
    private void setupIntegration() {
        // Связываем компоненты с агентом метрик
        metricsAgent.setReplicationManager(replicationManager);
        metricsAgent.setFailureDetector(failureDetector);
        metricsAgent.setRecoveryManager(recoveryManager);
        metricsAgent.setLoadBalancer(loadBalancer);
        
        // Настраиваем конфигурации
        replicationManager.configure(config.getReplicationConfig());
        failureDetector.configure(config.getFailureDetectionConfig());
        recoveryManager.configure(config.getRecoveryConfig());
        loadBalancer.configure(config.getLoadBalancingConfig());
    }
    
    @Override
    public void start() {
        super.start();
        
        // Запускаем новые компоненты
        replicationManager.start();
        failureDetector.start();
        recoveryManager.start();
        loadBalancer.start();
        metricsAgent.start();
        
        System.out.println("ScalableDHTNode started with enhanced capabilities");
    }
    
    @Override
    public void stop() {
        // Останавливаем новые компоненты
        metricsAgent.stop();
        loadBalancer.stop();
        recoveryManager.stop();
        failureDetector.stop();
        replicationManager.stop();
        
        super.stop();
        
        System.out.println("ScalableDHTNode stopped");
    }
    
    @Override
    public CompletableFuture<Boolean> store(String key, String value) {
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Записываем событие в метрики
                metricsAgent.recordOperation("STORE");
                
                // Получаем список узлов для репликации
                List<NodeInfo> candidateNodes = findNodesForKey(key);
                
                // Используем балансировщик для выбора оптимальных узлов
                List<NodeInfo> selectedNodes = loadBalancer.selectNodes(
                    OperationType.STORE, key, candidateNodes, config.getReplicationFactor());
                
                if (selectedNodes.isEmpty()) {
                    metricsAgent.recordLoadBalancingEvent(false, System.currentTimeMillis() - startTime);
                    return false;
                }
                
                // Выполняем репликацию
                ReplicationResult result = replicationManager.replicate(key, value, selectedNodes).join();
                
                long duration = System.currentTimeMillis() - startTime;
                
                // Записываем результат в метрики
                metricsAgent.recordReplicationEvent(result.isSuccess(), duration);
                metricsAgent.recordLoadBalancingEvent(result.isSuccess(), duration);
                
                return result.isSuccess();
                
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                metricsAgent.recordReplicationEvent(false, duration);
                metricsAgent.recordLoadBalancingEvent(false, duration);
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<String> findValue(String key) {
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Записываем событие в метрики
                metricsAgent.recordOperation("FIND_VALUE");
                
                // Получаем список узлов, которые могут содержать ключ
                List<NodeInfo> candidateNodes = findNodesForKey(key);
                
                // Используем балансировщик для выбора оптимального узла
                NodeInfo selectedNode = loadBalancer.selectNode(
                    OperationType.FIND_VALUE, key, candidateNodes);
                
                if (selectedNode == null) {
                    metricsAgent.recordLoadBalancingEvent(false, System.currentTimeMillis() - startTime);
                    return null;
                }
                
                // Выполняем поиск
                String value = performFindValue(key, selectedNode);
                
                long duration = System.currentTimeMillis() - startTime;
                metricsAgent.recordLoadBalancingEvent(value != null, duration);
                
                return value;
                
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                metricsAgent.recordLoadBalancingEvent(false, duration);
                return null;
            }
        });
    }
    
    private String performFindValue(String key, NodeInfo node) {
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
            DHTMessage response = getRpcClient().sendMessage(findMessage, node.getAddress(), node.getPort());
            
            if (response != null && response.getType() == MessageType.VALUE_RESPONSE) {
                return response.getValue();
            }
            
            return null;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private List<NodeInfo> findNodesForKey(String key) {
        // Получаем ближайшие узлы для ключа
        KademliaId keyId = KademliaId.fromString(key);
        List<NodeInfo> closestNodes = getRoutingTable().findClosestNodes(keyId, config.getReplicationFactor() * 2);
        
        // Фильтруем только здоровые узлы
        List<NodeInfo> healthyNodes = new ArrayList<>();
        for (NodeInfo node : closestNodes) {
            if (!failureDetector.getFailedNodes().contains(node)) {
                healthyNodes.add(node);
            }
        }
        
        return healthyNodes;
    }
    
    @Override
    public void addNode(NodeInfo node) {
        super.addNode(node);
        
        // Добавляем узел в детектор отказов
        failureDetector.addNode(node);
        
        // Обновляем метрики нагрузки
        loadBalancer.updateNodeLoad(node, NodeLoadMetrics.empty());
    }
    
    @Override
    public void removeNode(NodeInfo node) {
        super.removeNode(node);
        
        // Удаляем узел из детектора отказов
        failureDetector.removeNode(node);
    }
    
    /**
     * Получает статистику масштабирования
     */
    public ScalabilityStats getScalabilityStats() {
        ReplicationStats replicationStats = replicationManager.getStats();
        FailureDetectionStats failureStats = failureDetector.getStats();
        RecoveryStats recoveryStats = recoveryManager.getRecoveryStats();
        LoadBalancingStats loadBalancingStats = loadBalancer.getStats();
        
        return new ScalabilityStats(
            replicationStats,
            failureStats,
            recoveryStats,
            loadBalancingStats,
            System.currentTimeMillis()
        );
    }
    
    /**
     * Получает сводку метрик
     */
    public String getMetricsSummary() {
        return metricsAgent.getMetricsSummary();
    }
    
    /**
     * Проверяет здоровье системы
     */
    public SystemHealthReport getSystemHealth() {
        ScalabilityStats stats = getScalabilityStats();
        
        boolean isHealthy = true;
        List<String> issues = new ArrayList<>();
        
        // Проверяем репликацию
        if (stats.getReplicationStats().getSuccessRate() < 95.0) {
            isHealthy = false;
            issues.add("Low replication success rate: " + 
                      String.format("%.1f%%", stats.getReplicationStats().getSuccessRate()));
        }
        
        // Проверяем доступность сети
        if (stats.getFailureStats().getNetworkAvailability() < 90.0) {
            isHealthy = false;
            issues.add("Low network availability: " + 
                      String.format("%.1f%%", stats.getFailureStats().getNetworkAvailability()));
        }
        
        // Проверяем балансировку нагрузки
        if (stats.getLoadBalancingStats().getEfficiencyScore() < 80.0) {
            isHealthy = false;
            issues.add("Low load balancing efficiency: " + 
                      String.format("%.1f", stats.getLoadBalancingStats().getEfficiencyScore()));
        }
        
        return new SystemHealthReport(isHealthy, issues, stats);
    }
    
    // Геттеры для компонентов
    public ReplicationManager getReplicationManager() { return replicationManager; }
    public FailureDetector getFailureDetector() { return failureDetector; }
    public RecoveryManager getRecoveryManager() { return recoveryManager; }
    public LoadBalancer getLoadBalancer() { return loadBalancer; }
    public DHTMetricsAgent getMetricsAgent() { return metricsAgent; }
    public ScalabilityConfig getConfig() { return config; }
}

