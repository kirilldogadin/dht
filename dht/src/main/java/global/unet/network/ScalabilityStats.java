package global.unet.network;

import global.unet.replication.ReplicationStats;
import global.unet.failure.FailureDetectionStats;
import global.unet.failure.RecoveryStats;
import global.unet.loadbalancing.LoadBalancingStats;

/**
 * Сводная статистика масштабирования DHT системы
 */
public class ScalabilityStats {
    
    private final ReplicationStats replicationStats;
    private final FailureDetectionStats failureStats;
    private final RecoveryStats recoveryStats;
    private final LoadBalancingStats loadBalancingStats;
    private final long timestamp;
    
    public ScalabilityStats(ReplicationStats replicationStats,
                          FailureDetectionStats failureStats,
                          RecoveryStats recoveryStats,
                          LoadBalancingStats loadBalancingStats,
                          long timestamp) {
        this.replicationStats = replicationStats;
        this.failureStats = failureStats;
        this.recoveryStats = recoveryStats;
        this.loadBalancingStats = loadBalancingStats;
        this.timestamp = timestamp;
    }
    
    // Геттеры
    public ReplicationStats getReplicationStats() { return replicationStats; }
    public FailureDetectionStats getFailureStats() { return failureStats; }
    public RecoveryStats getRecoveryStats() { return recoveryStats; }
    public LoadBalancingStats getLoadBalancingStats() { return loadBalancingStats; }
    public long getTimestamp() { return timestamp; }
    
    /**
     * Вычисляет общий балл масштабируемости (0-100)
     */
    public double getScalabilityScore() {
        double replicationScore = replicationStats.getSuccessRate();
        double availabilityScore = failureStats.getNetworkAvailability();
        double recoveryScore = recoveryStats.getRecoverySuccessRate();
        double loadBalancingScore = loadBalancingStats.getEfficiencyScore();
        
        // Взвешенная сумма различных показателей
        return (replicationScore * 0.25) + (availabilityScore * 0.25) + 
               (recoveryScore * 0.25) + (loadBalancingScore * 0.25);
    }
    
    /**
     * Вычисляет общую пропускную способность системы
     */
    public double getThroughput() {
        // Упрощенный расчет на основе количества успешных операций
        long totalOperations = replicationStats.getSuccessfulReplications() + 
                              loadBalancingStats.getBalancedRequests();
        
        // Операций в секунду (примерно)
        return totalOperations / Math.max(1, (System.currentTimeMillis() - timestamp) / 1000.0);
    }
    
    /**
     * Вычисляет среднюю задержку системы
     */
    public double getAverageLatency() {
        double replicationLatency = replicationStats.getAverageReplicationTime();
        double responseLatency = loadBalancingStats.getAverageResponseTime();
        
        return (replicationLatency + responseLatency) / 2.0;
    }
    
    /**
     * Получает уровень отказоустойчивости
     */
    public String getResilienceLevel() {
        double availability = failureStats.getNetworkAvailability();
        double recoveryRate = recoveryStats.getRecoverySuccessRate();
        
        double resilienceScore = (availability + recoveryRate) / 2.0;
        
        if (resilienceScore >= 95.0) return "EXCELLENT";
        if (resilienceScore >= 90.0) return "GOOD";
        if (resilienceScore >= 80.0) return "FAIR";
        if (resilienceScore >= 70.0) return "POOR";
        return "CRITICAL";
    }
    
    @Override
    public String toString() {
        return String.format(
            "ScalabilityStats{score=%.1f, throughput=%.1f ops/s, latency=%.1fms, resilience=%s, " +
            "replication=%.1f%%, availability=%.1f%%, recovery=%.1f%%, loadBalancing=%.1f}",
            getScalabilityScore(), getThroughput(), getAverageLatency(), getResilienceLevel(),
            replicationStats.getSuccessRate(), failureStats.getNetworkAvailability(),
            recoveryStats.getRecoverySuccessRate(), loadBalancingStats.getEfficiencyScore()
        );
    }
}

