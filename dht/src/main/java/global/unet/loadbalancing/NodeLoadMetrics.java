package global.unet.loadbalancing;

/**
 * Метрики нагрузки узла
 */
public class NodeLoadMetrics {
    
    private final double cpuUsage; // Использование CPU (0.0 - 1.0)
    private final double memoryUsage; // Использование памяти (0.0 - 1.0)
    private final double networkUsage; // Использование сети (0.0 - 1.0)
    private final int activeConnections; // Количество активных соединений
    private final int queueSize; // Размер очереди запросов
    private final double averageResponseTime; // Среднее время отклика (мс)
    private final long requestsPerSecond; // Запросов в секунду
    private final long timestamp; // Время измерения
    
    public NodeLoadMetrics(double cpuUsage, double memoryUsage, double networkUsage,
                          int activeConnections, int queueSize, double averageResponseTime,
                          long requestsPerSecond, long timestamp) {
        this.cpuUsage = Math.max(0.0, Math.min(1.0, cpuUsage));
        this.memoryUsage = Math.max(0.0, Math.min(1.0, memoryUsage));
        this.networkUsage = Math.max(0.0, Math.min(1.0, networkUsage));
        this.activeConnections = Math.max(0, activeConnections);
        this.queueSize = Math.max(0, queueSize);
        this.averageResponseTime = Math.max(0.0, averageResponseTime);
        this.requestsPerSecond = Math.max(0, requestsPerSecond);
        this.timestamp = timestamp;
    }
    
    // Конструктор с текущим временем
    public NodeLoadMetrics(double cpuUsage, double memoryUsage, double networkUsage,
                          int activeConnections, int queueSize, double averageResponseTime,
                          long requestsPerSecond) {
        this(cpuUsage, memoryUsage, networkUsage, activeConnections, queueSize,
             averageResponseTime, requestsPerSecond, System.currentTimeMillis());
    }
    
    // Конструктор для создания пустых метрик
    public static NodeLoadMetrics empty() {
        return new NodeLoadMetrics(0.0, 0.0, 0.0, 0, 0, 0.0, 0);
    }
    
    // Геттеры
    public double getCpuUsage() { return cpuUsage; }
    public double getMemoryUsage() { return memoryUsage; }
    public double getNetworkUsage() { return networkUsage; }
    public int getActiveConnections() { return activeConnections; }
    public int getQueueSize() { return queueSize; }
    public double getAverageResponseTime() { return averageResponseTime; }
    public long getRequestsPerSecond() { return requestsPerSecond; }
    public long getTimestamp() { return timestamp; }
    
    /**
     * Вычисляет общий балл нагрузки (0.0 - 1.0)
     */
    public double getOverallLoad() {
        // Взвешенная сумма различных метрик
        double load = (cpuUsage * 0.3) + 
                     (memoryUsage * 0.2) + 
                     (networkUsage * 0.2) + 
                     (Math.min(1.0, activeConnections / 100.0) * 0.15) +
                     (Math.min(1.0, queueSize / 50.0) * 0.15);
        
        return Math.max(0.0, Math.min(1.0, load));
    }
    
    /**
     * Проверяет, устарели ли метрики
     */
    public boolean isStale(long maxAgeMs) {
        return (System.currentTimeMillis() - timestamp) > maxAgeMs;
    }
    
    /**
     * Создает новые метрики с обновленным временем отклика
     */
    public NodeLoadMetrics withUpdatedResponseTime(double newResponseTime, long newRequestsPerSecond) {
        return new NodeLoadMetrics(
            cpuUsage, memoryUsage, networkUsage, activeConnections, queueSize,
            newResponseTime, newRequestsPerSecond, System.currentTimeMillis()
        );
    }
    
    @Override
    public String toString() {
        return String.format(
            "NodeLoadMetrics{cpu=%.1f%%, memory=%.1f%%, network=%.1f%%, " +
            "connections=%d, queue=%d, responseTime=%.1fms, rps=%d, overall=%.1f%%}",
            cpuUsage * 100, memoryUsage * 100, networkUsage * 100,
            activeConnections, queueSize, averageResponseTime, requestsPerSecond,
            getOverallLoad() * 100
        );
    }
}

