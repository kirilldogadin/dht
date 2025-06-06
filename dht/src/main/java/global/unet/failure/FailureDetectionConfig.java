package global.unet.failure;

/**
 * Конфигурация для детекции отказов
 */
public class FailureDetectionConfig {
    
    private final int heartbeatIntervalMs;
    private final int timeoutMs;
    private final int maxRetries;
    private final int suspicionTimeoutMs;
    private final boolean enableNetworkPartitionDetection;
    
    public FailureDetectionConfig(int heartbeatIntervalMs, int timeoutMs, int maxRetries,
                                int suspicionTimeoutMs, boolean enableNetworkPartitionDetection) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.timeoutMs = timeoutMs;
        this.maxRetries = maxRetries;
        this.suspicionTimeoutMs = suspicionTimeoutMs;
        this.enableNetworkPartitionDetection = enableNetworkPartitionDetection;
    }
    
    // Конструктор с настройками по умолчанию
    public FailureDetectionConfig() {
        this(5000, 10000, 3, 30000, true);
    }
    
    // Геттеры
    public int getHeartbeatIntervalMs() { return heartbeatIntervalMs; }
    public int getTimeoutMs() { return timeoutMs; }
    public int getMaxRetries() { return maxRetries; }
    public int getSuspicionTimeoutMs() { return suspicionTimeoutMs; }
    public boolean isNetworkPartitionDetectionEnabled() { return enableNetworkPartitionDetection; }
    
    @Override
    public String toString() {
        return String.format(
            "FailureDetectionConfig{heartbeat=%dms, timeout=%dms, retries=%d, suspicion=%dms, partition=%s}",
            heartbeatIntervalMs, timeoutMs, maxRetries, suspicionTimeoutMs, enableNetworkPartitionDetection
        );
    }
}

