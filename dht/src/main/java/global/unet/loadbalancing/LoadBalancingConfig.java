package global.unet.loadbalancing;

/**
 * Конфигурация для балансировки нагрузки
 */
public class LoadBalancingConfig {
    
    private final double overloadThreshold; // Порог перегрузки (0.0 - 1.0)
    private final double underloadThreshold; // Порог недогрузки (0.0 - 1.0)
    private final int metricsUpdateIntervalMs; // Интервал обновления метрик
    private final int metricsMaxAgeMs; // Максимальный возраст метрик
    private final LoadBalancingStrategy strategy; // Стратегия балансировки
    private final boolean enableAdaptiveRouting; // Включить адаптивную маршрутизацию
    private final boolean enableCaching; // Включить кэширование
    private final int cacheSize; // Размер кэша
    private final int cacheTtlMs; // TTL кэша
    
    public LoadBalancingConfig(double overloadThreshold, double underloadThreshold,
                             int metricsUpdateIntervalMs, int metricsMaxAgeMs,
                             LoadBalancingStrategy strategy, boolean enableAdaptiveRouting,
                             boolean enableCaching, int cacheSize, int cacheTtlMs) {
        this.overloadThreshold = Math.max(0.0, Math.min(1.0, overloadThreshold));
        this.underloadThreshold = Math.max(0.0, Math.min(1.0, underloadThreshold));
        this.metricsUpdateIntervalMs = Math.max(1000, metricsUpdateIntervalMs);
        this.metricsMaxAgeMs = Math.max(5000, metricsMaxAgeMs);
        this.strategy = strategy;
        this.enableAdaptiveRouting = enableAdaptiveRouting;
        this.enableCaching = enableCaching;
        this.cacheSize = Math.max(10, cacheSize);
        this.cacheTtlMs = Math.max(1000, cacheTtlMs);
    }
    
    // Конструктор с настройками по умолчанию
    public LoadBalancingConfig() {
        this(0.8, 0.3, 5000, 30000, LoadBalancingStrategy.WEIGHTED_ROUND_ROBIN,
             true, true, 1000, 60000);
    }
    
    // Геттеры
    public double getOverloadThreshold() { return overloadThreshold; }
    public double getUnderloadThreshold() { return underloadThreshold; }
    public int getMetricsUpdateIntervalMs() { return metricsUpdateIntervalMs; }
    public int getMetricsMaxAgeMs() { return metricsMaxAgeMs; }
    public LoadBalancingStrategy getStrategy() { return strategy; }
    public boolean isAdaptiveRoutingEnabled() { return enableAdaptiveRouting; }
    public boolean isCachingEnabled() { return enableCaching; }
    public int getCacheSize() { return cacheSize; }
    public int getCacheTtlMs() { return cacheTtlMs; }
    
    @Override
    public String toString() {
        return String.format(
            "LoadBalancingConfig{overload=%.1f%%, underload=%.1f%%, updateInterval=%dms, " +
            "maxAge=%dms, strategy=%s, adaptive=%s, cache=%s/%d/%dms}",
            overloadThreshold * 100, underloadThreshold * 100, metricsUpdateIntervalMs,
            metricsMaxAgeMs, strategy, enableAdaptiveRouting, enableCaching, cacheSize, cacheTtlMs
        );
    }
}

