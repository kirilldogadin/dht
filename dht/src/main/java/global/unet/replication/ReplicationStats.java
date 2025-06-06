package global.unet.replication;

/**
 * Статистика репликации данных
 */
public class ReplicationStats {
    
    private final long totalKeys;
    private final long totalReplicas;
    private final double averageReplicationFactor;
    private final long inconsistentReplicas;
    private final long failedReplications;
    private final long successfulReplications;
    private final double replicationSuccessRate;
    private final long lastSyncTime;
    
    public ReplicationStats(long totalKeys, long totalReplicas, double averageReplicationFactor,
                           long inconsistentReplicas, long failedReplications, 
                           long successfulReplications, long lastSyncTime) {
        this.totalKeys = totalKeys;
        this.totalReplicas = totalReplicas;
        this.averageReplicationFactor = averageReplicationFactor;
        this.inconsistentReplicas = inconsistentReplicas;
        this.failedReplications = failedReplications;
        this.successfulReplications = successfulReplications;
        this.replicationSuccessRate = (double) successfulReplications / 
                                     (successfulReplications + failedReplications) * 100.0;
        this.lastSyncTime = lastSyncTime;
    }
    
    // Геттеры
    public long getTotalKeys() { return totalKeys; }
    public long getTotalReplicas() { return totalReplicas; }
    public double getAverageReplicationFactor() { return averageReplicationFactor; }
    public long getInconsistentReplicas() { return inconsistentReplicas; }
    public long getFailedReplications() { return failedReplications; }
    public long getSuccessfulReplications() { return successfulReplications; }
    public double getReplicationSuccessRate() { return replicationSuccessRate; }
    public long getLastSyncTime() { return lastSyncTime; }
    
    @Override
    public String toString() {
        return String.format(
            "ReplicationStats{totalKeys=%d, totalReplicas=%d, avgReplicationFactor=%.2f, " +
            "inconsistentReplicas=%d, successRate=%.2f%%, lastSync=%d}",
            totalKeys, totalReplicas, averageReplicationFactor, 
            inconsistentReplicas, replicationSuccessRate, lastSyncTime
        );
    }
}

