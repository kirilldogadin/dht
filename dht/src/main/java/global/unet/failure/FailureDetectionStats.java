package global.unet.failure;

/**
 * Статистика детекции отказов
 */
public class FailureDetectionStats {
    
    private final int totalNodes;
    private final int healthyNodes;
    private final int failedNodes;
    private final int suspiciousNodes;
    private final long totalFailures;
    private final long totalRecoveries;
    private final long networkPartitions;
    private final double averageDetectionTimeMs;
    private final double falsePositiveRate;
    private final long lastCheckTime;
    
    public FailureDetectionStats(int totalNodes, int healthyNodes, int failedNodes, int suspiciousNodes,
                               long totalFailures, long totalRecoveries, long networkPartitions,
                               double averageDetectionTimeMs, double falsePositiveRate, long lastCheckTime) {
        this.totalNodes = totalNodes;
        this.healthyNodes = healthyNodes;
        this.failedNodes = failedNodes;
        this.suspiciousNodes = suspiciousNodes;
        this.totalFailures = totalFailures;
        this.totalRecoveries = totalRecoveries;
        this.networkPartitions = networkPartitions;
        this.averageDetectionTimeMs = averageDetectionTimeMs;
        this.falsePositiveRate = falsePositiveRate;
        this.lastCheckTime = lastCheckTime;
    }
    
    // Геттеры
    public int getTotalNodes() { return totalNodes; }
    public int getHealthyNodes() { return healthyNodes; }
    public int getFailedNodes() { return failedNodes; }
    public int getSuspiciousNodes() { return suspiciousNodes; }
    public long getTotalFailures() { return totalFailures; }
    public long getTotalRecoveries() { return totalRecoveries; }
    public long getNetworkPartitions() { return networkPartitions; }
    public double getAverageDetectionTimeMs() { return averageDetectionTimeMs; }
    public double getFalsePositiveRate() { return falsePositiveRate; }
    public long getLastCheckTime() { return lastCheckTime; }
    
    /**
     * Получает процент доступности сети
     */
    public double getNetworkAvailability() {
        if (totalNodes == 0) return 100.0;
        return (double) healthyNodes / totalNodes * 100.0;
    }
    
    /**
     * Получает коэффициент восстановления
     */
    public double getRecoveryRate() {
        if (totalFailures == 0) return 0.0;
        return (double) totalRecoveries / totalFailures * 100.0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "FailureDetectionStats{nodes=%d/%d healthy, failures=%d, recoveries=%d, " +
            "availability=%.1f%%, avgDetection=%.1fms, falsePositive=%.2f%%}",
            healthyNodes, totalNodes, totalFailures, totalRecoveries,
            getNetworkAvailability(), averageDetectionTimeMs, falsePositiveRate
        );
    }
}

