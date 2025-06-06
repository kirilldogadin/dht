package global.unet.failure;

/**
 * Статистика восстановления данных
 */
public class RecoveryStats {
    
    private final long totalRecoveries;
    private final long successfulRecoveries;
    private final long failedRecoveries;
    private final long totalKeysRecovered;
    private final long totalKeysLost;
    private final double averageRecoveryTimeMs;
    private final double dataRecoveryRate;
    private final long lastRecoveryTime;
    private final int activeRecoveries;
    
    public RecoveryStats(long totalRecoveries, long successfulRecoveries, long failedRecoveries,
                       long totalKeysRecovered, long totalKeysLost, double averageRecoveryTimeMs,
                       long lastRecoveryTime, int activeRecoveries) {
        this.totalRecoveries = totalRecoveries;
        this.successfulRecoveries = successfulRecoveries;
        this.failedRecoveries = failedRecoveries;
        this.totalKeysRecovered = totalKeysRecovered;
        this.totalKeysLost = totalKeysLost;
        this.averageRecoveryTimeMs = averageRecoveryTimeMs;
        this.lastRecoveryTime = lastRecoveryTime;
        this.activeRecoveries = activeRecoveries;
        
        // Вычисляем процент восстановления данных
        long totalKeys = totalKeysRecovered + totalKeysLost;
        this.dataRecoveryRate = totalKeys > 0 ? (double) totalKeysRecovered / totalKeys * 100.0 : 100.0;
    }
    
    // Геттеры
    public long getTotalRecoveries() { return totalRecoveries; }
    public long getSuccessfulRecoveries() { return successfulRecoveries; }
    public long getFailedRecoveries() { return failedRecoveries; }
    public long getTotalKeysRecovered() { return totalKeysRecovered; }
    public long getTotalKeysLost() { return totalKeysLost; }
    public double getAverageRecoveryTimeMs() { return averageRecoveryTimeMs; }
    public double getDataRecoveryRate() { return dataRecoveryRate; }
    public long getLastRecoveryTime() { return lastRecoveryTime; }
    public int getActiveRecoveries() { return activeRecoveries; }
    
    /**
     * Получает процент успешности операций восстановления
     */
    public double getRecoverySuccessRate() {
        if (totalRecoveries == 0) return 100.0;
        return (double) successfulRecoveries / totalRecoveries * 100.0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "RecoveryStats{recoveries=%d/%d (%.1f%%), keys=%d/%d (%.1f%%), avgTime=%.1fms, active=%d}",
            successfulRecoveries, totalRecoveries, getRecoverySuccessRate(),
            totalKeysRecovered, totalKeysRecovered + totalKeysLost, dataRecoveryRate,
            averageRecoveryTimeMs, activeRecoveries
        );
    }
}

