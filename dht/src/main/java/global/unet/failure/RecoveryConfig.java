package global.unet.failure;

/**
 * Конфигурация для восстановления данных
 */
public class RecoveryConfig {
    
    private final int recoveryThreads;
    private final int maxConcurrentRecoveries;
    private final int recoveryTimeoutMs;
    private final int rebalanceIntervalMs;
    private final boolean autoRebalance;
    private final int targetReplicationFactor;
    private final boolean prioritizeHotData;
    
    public RecoveryConfig(int recoveryThreads, int maxConcurrentRecoveries, int recoveryTimeoutMs,
                        int rebalanceIntervalMs, boolean autoRebalance, int targetReplicationFactor,
                        boolean prioritizeHotData) {
        this.recoveryThreads = recoveryThreads;
        this.maxConcurrentRecoveries = maxConcurrentRecoveries;
        this.recoveryTimeoutMs = recoveryTimeoutMs;
        this.rebalanceIntervalMs = rebalanceIntervalMs;
        this.autoRebalance = autoRebalance;
        this.targetReplicationFactor = targetReplicationFactor;
        this.prioritizeHotData = prioritizeHotData;
    }
    
    // Конструктор с настройками по умолчанию
    public RecoveryConfig() {
        this(4, 2, 60000, 300000, true, 3, true);
    }
    
    // Геттеры
    public int getRecoveryThreads() { return recoveryThreads; }
    public int getMaxConcurrentRecoveries() { return maxConcurrentRecoveries; }
    public int getRecoveryTimeoutMs() { return recoveryTimeoutMs; }
    public int getRebalanceIntervalMs() { return rebalanceIntervalMs; }
    public boolean isAutoRebalanceEnabled() { return autoRebalance; }
    public int getTargetReplicationFactor() { return targetReplicationFactor; }
    public boolean isPrioritizeHotDataEnabled() { return prioritizeHotData; }
    
    @Override
    public String toString() {
        return String.format(
            "RecoveryConfig{threads=%d, concurrent=%d, timeout=%dms, rebalance=%dms, auto=%s, factor=%d, hotData=%s}",
            recoveryThreads, maxConcurrentRecoveries, recoveryTimeoutMs, rebalanceIntervalMs,
            autoRebalance, targetReplicationFactor, prioritizeHotData
        );
    }
}

