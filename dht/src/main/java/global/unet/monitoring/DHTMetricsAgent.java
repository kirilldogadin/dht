package global.unet.monitoring;

import global.unet.monitoring.metrics.*;
import global.unet.monitoring.export.PrometheusExporter;
import global.unet.replication.ReplicationManager;
import global.unet.replication.ReplicationStats;
import global.unet.failure.FailureDetector;
import global.unet.failure.FailureDetectionStats;
import global.unet.failure.RecoveryManager;
import global.unet.failure.RecoveryStats;
import global.unet.loadbalancing.LoadBalancer;
import global.unet.loadbalancing.LoadBalancingStats;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Расширенный агент метрик DHT с поддержкой масштабирования и отказоустойчивости
 */
public class DHTMetricsAgent {
    
    private final String nodeId;
    private final String serverUrl;
    private final MetricsRegistry metricsRegistry;
    private final PrometheusExporter prometheusExporter;
    private final ScheduledExecutorService scheduler;
    
    // Компоненты для мониторинга
    private ReplicationManager replicationManager;
    private FailureDetector failureDetector;
    private RecoveryManager recoveryManager;
    private LoadBalancer loadBalancer;
    
    // Метрики репликации
    private Counter replicationRequests;
    private Counter replicationSuccesses;
    private Counter replicationFailures;
    private Histogram replicationLatency;
    private Gauge replicationFactor;
    
    // Метрики отказов
    private Counter nodeFailures;
    private Counter nodeRecoveries;
    private Gauge healthyNodes;
    private Gauge failedNodes;
    private Histogram failureDetectionTime;
    
    // Метрики восстановления
    private Counter recoveryOperations;
    private Counter recoveredKeys;
    private Counter lostKeys;
    private Histogram recoveryTime;
    private Gauge dataIntegrityScore;
    
    // Метрики балансировки нагрузки
    private Counter loadBalancingRequests;
    private Counter rejectedRequests;
    private Histogram responseTime;
    private Gauge overloadedNodes;
    private Gauge cacheHitRate;
    
    // Общие метрики производительности
    private Gauge networkAvailability;
    private Gauge systemEfficiency;
    private Counter totalOperations;
    
    private volatile boolean running = false;
    
    public DHTMetricsAgent(String nodeId, String serverUrl) {
        this.nodeId = nodeId;
        this.serverUrl = serverUrl;
        this.metricsRegistry = new MetricsRegistry();
        this.prometheusExporter = new PrometheusExporter(metricsRegistry);
        this.scheduler = Executors.newScheduledThreadPool(2);
        
        initializeMetrics();
    }
    
    private void initializeMetrics() {
        // Метрики репликации
        replicationRequests = metricsRegistry.counter("dht_replication_requests_total", 
            "Total number of replication requests");
        replicationSuccesses = metricsRegistry.counter("dht_replication_successes_total", 
            "Total number of successful replications");
        replicationFailures = metricsRegistry.counter("dht_replication_failures_total", 
            "Total number of failed replications");
        replicationLatency = metricsRegistry.histogram("dht_replication_latency_ms", 
            "Replication operation latency in milliseconds");
        replicationFactor = metricsRegistry.gauge("dht_replication_factor", 
            "Current replication factor");
        
        // Метрики отказов
        nodeFailures = metricsRegistry.counter("dht_node_failures_total", 
            "Total number of node failures detected");
        nodeRecoveries = metricsRegistry.counter("dht_node_recoveries_total", 
            "Total number of node recoveries");
        healthyNodes = metricsRegistry.gauge("dht_healthy_nodes", 
            "Number of healthy nodes in the network");
        failedNodes = metricsRegistry.gauge("dht_failed_nodes", 
            "Number of failed nodes in the network");
        failureDetectionTime = metricsRegistry.histogram("dht_failure_detection_time_ms", 
            "Time to detect node failures in milliseconds");
        
        // Метрики восстановления
        recoveryOperations = metricsRegistry.counter("dht_recovery_operations_total", 
            "Total number of recovery operations");
        recoveredKeys = metricsRegistry.counter("dht_recovered_keys_total", 
            "Total number of recovered keys");
        lostKeys = metricsRegistry.counter("dht_lost_keys_total", 
            "Total number of lost keys");
        recoveryTime = metricsRegistry.histogram("dht_recovery_time_ms", 
            "Recovery operation time in milliseconds");
        dataIntegrityScore = metricsRegistry.gauge("dht_data_integrity_score", 
            "Data integrity score (0-100)");
        
        // Метрики балансировки нагрузки
        loadBalancingRequests = metricsRegistry.counter("dht_load_balancing_requests_total", 
            "Total number of load balancing requests");
        rejectedRequests = metricsRegistry.counter("dht_rejected_requests_total", 
            "Total number of rejected requests due to overload");
        responseTime = metricsRegistry.histogram("dht_response_time_ms", 
            "Response time in milliseconds");
        overloadedNodes = metricsRegistry.gauge("dht_overloaded_nodes", 
            "Number of overloaded nodes");
        cacheHitRate = metricsRegistry.gauge("dht_cache_hit_rate", 
            "Cache hit rate percentage");
        
        // Общие метрики
        networkAvailability = metricsRegistry.gauge("dht_network_availability", 
            "Network availability percentage");
        systemEfficiency = metricsRegistry.gauge("dht_system_efficiency", 
            "Overall system efficiency score");
        totalOperations = metricsRegistry.counter("dht_total_operations", 
            "Total number of DHT operations");
    }
    
    public void setReplicationManager(ReplicationManager replicationManager) {
        this.replicationManager = replicationManager;
    }
    
    public void setFailureDetector(FailureDetector failureDetector) {
        this.failureDetector = failureDetector;
    }
    
    public void setRecoveryManager(RecoveryManager recoveryManager) {
        this.recoveryManager = recoveryManager;
    }
    
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    public void start() {
        if (running) return;
        
        running = true;
        
        // Запускаем периодический сбор метрик
        scheduler.scheduleAtFixedRate(this::collectMetrics, 0, 10, TimeUnit.SECONDS);
        
        // Запускаем экспорт метрик
        scheduler.scheduleAtFixedRate(this::exportMetrics, 5, 30, TimeUnit.SECONDS);
        
        System.out.println("Enhanced DHTMetricsAgent started for node: " + nodeId);
    }
    
    public void stop() {
        if (!running) return;
        
        running = false;
        scheduler.shutdown();
        
        System.out.println("Enhanced DHTMetricsAgent stopped for node: " + nodeId);
    }
    
    private void collectMetrics() {
        try {
            collectReplicationMetrics();
            collectFailureDetectionMetrics();
            collectRecoveryMetrics();
            collectLoadBalancingMetrics();
            collectGeneralMetrics();
        } catch (Exception e) {
            System.err.println("Error collecting metrics: " + e.getMessage());
        }
    }
    
    private void collectReplicationMetrics() {
        if (replicationManager == null) return;
        
        try {
            ReplicationStats stats = replicationManager.getStats();
            
            // Обновляем счетчики
            replicationRequests.increment(stats.getTotalReplications() - replicationRequests.getValue());
            replicationSuccesses.increment(stats.getSuccessfulReplications() - replicationSuccesses.getValue());
            replicationFailures.increment(stats.getFailedReplications() - replicationFailures.getValue());
            
            // Обновляем гистограммы и gauge
            replicationLatency.observe(stats.getAverageReplicationTime());
            replicationFactor.setValue(stats.getAverageReplicationFactor());
            
        } catch (Exception e) {
            System.err.println("Error collecting replication metrics: " + e.getMessage());
        }
    }
    
    private void collectFailureDetectionMetrics() {
        if (failureDetector == null) return;
        
        try {
            FailureDetectionStats stats = failureDetector.getStats();
            
            // Обновляем счетчики
            nodeFailures.increment(stats.getTotalFailures() - nodeFailures.getValue());
            nodeRecoveries.increment(stats.getTotalRecoveries() - nodeRecoveries.getValue());
            
            // Обновляем gauge
            healthyNodes.setValue(stats.getHealthyNodes());
            failedNodes.setValue(stats.getFailedNodes());
            
            // Обновляем гистограммы
            failureDetectionTime.observe(stats.getAverageDetectionTimeMs());
            
        } catch (Exception e) {
            System.err.println("Error collecting failure detection metrics: " + e.getMessage());
        }
    }
    
    private void collectRecoveryMetrics() {
        if (recoveryManager == null) return;
        
        try {
            RecoveryStats stats = recoveryManager.getRecoveryStats();
            
            // Обновляем счетчики
            recoveryOperations.increment(stats.getTotalRecoveries() - recoveryOperations.getValue());
            recoveredKeys.increment(stats.getTotalKeysRecovered() - recoveredKeys.getValue());
            lostKeys.increment(stats.getTotalKeysLost() - lostKeys.getValue());
            
            // Обновляем гистограммы и gauge
            recoveryTime.observe(stats.getAverageRecoveryTimeMs());
            dataIntegrityScore.setValue(stats.getDataRecoveryRate());
            
        } catch (Exception e) {
            System.err.println("Error collecting recovery metrics: " + e.getMessage());
        }
    }
    
    private void collectLoadBalancingMetrics() {
        if (loadBalancer == null) return;
        
        try {
            LoadBalancingStats stats = loadBalancer.getStats();
            
            // Обновляем счетчики
            loadBalancingRequests.increment(stats.getTotalRequests() - loadBalancingRequests.getValue());
            rejectedRequests.increment(stats.getRejectedRequests() - rejectedRequests.getValue());
            
            // Обновляем гистограммы и gauge
            responseTime.observe(stats.getAverageResponseTime());
            overloadedNodes.setValue(stats.getOverloadedNodes());
            cacheHitRate.setValue(stats.getCacheHitRate());
            
        } catch (Exception e) {
            System.err.println("Error collecting load balancing metrics: " + e.getMessage());
        }
    }
    
    private void collectGeneralMetrics() {
        try {
            // Вычисляем общую доступность сети
            double availability = 100.0;
            if (failureDetector != null) {
                FailureDetectionStats stats = failureDetector.getStats();
                availability = stats.getNetworkAvailability();
            }
            networkAvailability.setValue(availability);
            
            // Вычисляем общую эффективность системы
            double efficiency = calculateSystemEfficiency();
            systemEfficiency.setValue(efficiency);
            
            // Обновляем общий счетчик операций
            long currentOperations = 0;
            if (loadBalancer != null) {
                currentOperations = loadBalancer.getStats().getTotalRequests();
            }
            totalOperations.increment(currentOperations - totalOperations.getValue());
            
        } catch (Exception e) {
            System.err.println("Error collecting general metrics: " + e.getMessage());
        }
    }
    
    private double calculateSystemEfficiency() {
        double efficiency = 100.0;
        int components = 0;
        
        // Учитываем эффективность репликации
        if (replicationManager != null) {
            ReplicationStats stats = replicationManager.getStats();
            efficiency += stats.getSuccessRate();
            components++;
        }
        
        // Учитываем доступность сети
        if (failureDetector != null) {
            FailureDetectionStats stats = failureDetector.getStats();
            efficiency += stats.getNetworkAvailability();
            components++;
        }
        
        // Учитываем эффективность восстановления
        if (recoveryManager != null) {
            RecoveryStats stats = recoveryManager.getRecoveryStats();
            efficiency += stats.getRecoverySuccessRate();
            components++;
        }
        
        // Учитываем эффективность балансировки
        if (loadBalancer != null) {
            LoadBalancingStats stats = loadBalancer.getStats();
            efficiency += stats.getEfficiencyScore();
            components++;
        }
        
        return components > 0 ? efficiency / components : 100.0;
    }
    
    private void exportMetrics() {
        try {
            // Экспортируем метрики в формате Prometheus
            String prometheusData = prometheusExporter.export();
            
            // Отправляем на сервер мониторинга (упрощенная реализация)
            sendToMonitoringServer(prometheusData);
            
        } catch (Exception e) {
            System.err.println("Error exporting metrics: " + e.getMessage());
        }
    }
    
    private void sendToMonitoringServer(String data) {
        // Упрощенная реализация отправки данных
        System.out.println("Sending metrics to " + serverUrl + " for node " + nodeId);
        System.out.println("Metrics data size: " + data.length() + " bytes");
    }
    
    // Методы для записи специфических событий
    
    public void recordReplicationEvent(boolean success, long latencyMs) {
        if (success) {
            replicationSuccesses.increment();
        } else {
            replicationFailures.increment();
        }
        replicationLatency.observe(latencyMs);
        replicationRequests.increment();
    }
    
    public void recordFailureEvent(String nodeId, long detectionTimeMs) {
        nodeFailures.increment();
        failureDetectionTime.observe(detectionTimeMs);
    }
    
    public void recordRecoveryEvent(String nodeId) {
        nodeRecoveries.increment();
    }
    
    public void recordLoadBalancingEvent(boolean success, long responseTimeMs) {
        loadBalancingRequests.increment();
        if (!success) {
            rejectedRequests.increment();
        }
        responseTime.observe(responseTimeMs);
    }
    
    public void recordOperation(String operationType) {
        totalOperations.increment();
    }
    
    // Геттеры
    public String getNodeId() { return nodeId; }
    public String getServerUrl() { return serverUrl; }
    public MetricsRegistry getMetricsRegistry() { return metricsRegistry; }
    
    /**
     * Получает сводку всех метрик
     */
    public String getMetricsSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== DHT Metrics Summary for Node ").append(nodeId).append(" ===\n");
        
        // Репликация
        summary.append("Replication: ")
               .append(replicationSuccesses.getValue()).append("/")
               .append(replicationRequests.getValue()).append(" successful, ")
               .append("avg latency: ").append(String.format("%.1f", replicationLatency.getMean())).append("ms\n");
        
        // Отказы
        summary.append("Failures: ")
               .append(nodeFailures.getValue()).append(" detected, ")
               .append(healthyNodes.getValue()).append("/")
               .append(healthyNodes.getValue() + failedNodes.getValue()).append(" nodes healthy\n");
        
        // Восстановление
        summary.append("Recovery: ")
               .append(recoveredKeys.getValue()).append(" keys recovered, ")
               .append(lostKeys.getValue()).append(" keys lost, ")
               .append("integrity: ").append(String.format("%.1f", dataIntegrityScore.getValue())).append("%\n");
        
        // Балансировка
        summary.append("Load Balancing: ")
               .append(loadBalancingRequests.getValue()).append(" requests, ")
               .append(rejectedRequests.getValue()).append(" rejected, ")
               .append("cache hit rate: ").append(String.format("%.1f", cacheHitRate.getValue())).append("%\n");
        
        // Общие показатели
        summary.append("Overall: ")
               .append("availability: ").append(String.format("%.1f", networkAvailability.getValue())).append("%, ")
               .append("efficiency: ").append(String.format("%.1f", systemEfficiency.getValue())).append("%\n");
        
        return summary.toString();
    }
}

