package global.unet.loadbalancing;

import java.util.Map;

/**
 * Статистика балансировки нагрузки
 */
public class LoadBalancingStats {
    
    private final long totalRequests;
    private final long balancedRequests;
    private final long rejectedRequests;
    private final Map<String, Long> requestsByOperation; // операция -> количество
    private final Map<String, Long> requestsByNode; // узел -> количество
    private final double averageResponseTime;
    private final double loadDistributionVariance; // Дисперсия распределения нагрузки
    private final int overloadedNodes;
    private final int underloadedNodes;
    private final int totalNodes;
    private final long cacheHits;
    private final long cacheMisses;
    private final long lastUpdateTime;
    
    public LoadBalancingStats(long totalRequests, long balancedRequests, long rejectedRequests,
                            Map<String, Long> requestsByOperation, Map<String, Long> requestsByNode,
                            double averageResponseTime, double loadDistributionVariance,
                            int overloadedNodes, int underloadedNodes, int totalNodes,
                            long cacheHits, long cacheMisses, long lastUpdateTime) {
        this.totalRequests = totalRequests;
        this.balancedRequests = balancedRequests;
        this.rejectedRequests = rejectedRequests;
        this.requestsByOperation = requestsByOperation;
        this.requestsByNode = requestsByNode;
        this.averageResponseTime = averageResponseTime;
        this.loadDistributionVariance = loadDistributionVariance;
        this.overloadedNodes = overloadedNodes;
        this.underloadedNodes = underloadedNodes;
        this.totalNodes = totalNodes;
        this.cacheHits = cacheHits;
        this.cacheMisses = cacheMisses;
        this.lastUpdateTime = lastUpdateTime;
    }
    
    // Геттеры
    public long getTotalRequests() { return totalRequests; }
    public long getBalancedRequests() { return balancedRequests; }
    public long getRejectedRequests() { return rejectedRequests; }
    public Map<String, Long> getRequestsByOperation() { return requestsByOperation; }
    public Map<String, Long> getRequestsByNode() { return requestsByNode; }
    public double getAverageResponseTime() { return averageResponseTime; }
    public double getLoadDistributionVariance() { return loadDistributionVariance; }
    public int getOverloadedNodes() { return overloadedNodes; }
    public int getUnderloadedNodes() { return underloadedNodes; }
    public int getTotalNodes() { return totalNodes; }
    public long getCacheHits() { return cacheHits; }
    public long getCacheMisses() { return cacheMisses; }
    public long getLastUpdateTime() { return lastUpdateTime; }
    
    /**
     * Получает процент успешности балансировки
     */
    public double getBalancingSuccessRate() {
        if (totalRequests == 0) return 100.0;
        return (double) balancedRequests / totalRequests * 100.0;
    }
    
    /**
     * Получает процент отклоненных запросов
     */
    public double getRejectionRate() {
        if (totalRequests == 0) return 0.0;
        return (double) rejectedRequests / totalRequests * 100.0;
    }
    
    /**
     * Получает эффективность кэша
     */
    public double getCacheHitRate() {
        long totalCacheRequests = cacheHits + cacheMisses;
        if (totalCacheRequests == 0) return 0.0;
        return (double) cacheHits / totalCacheRequests * 100.0;
    }
    
    /**
     * Получает процент здоровых узлов
     */
    public double getHealthyNodesRate() {
        if (totalNodes == 0) return 100.0;
        int healthyNodes = totalNodes - overloadedNodes;
        return (double) healthyNodes / totalNodes * 100.0;
    }
    
    /**
     * Получает балл эффективности балансировки (0-100)
     */
    public double getEfficiencyScore() {
        double balancingScore = getBalancingSuccessRate();
        double healthScore = getHealthyNodesRate();
        double distributionScore = Math.max(0, 100 - (loadDistributionVariance * 100));
        double cacheScore = getCacheHitRate();
        
        // Взвешенная сумма различных показателей
        return (balancingScore * 0.3) + (healthScore * 0.3) + 
               (distributionScore * 0.2) + (cacheScore * 0.2);
    }
    
    @Override
    public String toString() {
        return String.format(
            "LoadBalancingStats{requests=%d/%d (%.1f%%), rejected=%d (%.1f%%), " +
            "avgResponse=%.1fms, overloaded=%d/%d, cache=%.1f%%, efficiency=%.1f}",
            balancedRequests, totalRequests, getBalancingSuccessRate(),
            rejectedRequests, getRejectionRate(), averageResponseTime,
            overloadedNodes, totalNodes, getCacheHitRate(), getEfficiencyScore()
        );
    }
}

