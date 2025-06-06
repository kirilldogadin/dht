package global.unet.network;

import java.util.List;

/**
 * Отчет о здоровье DHT системы
 */
public class SystemHealthReport {
    
    private final boolean isHealthy;
    private final List<String> issues;
    private final ScalabilityStats stats;
    private final long timestamp;
    
    public SystemHealthReport(boolean isHealthy, List<String> issues, ScalabilityStats stats) {
        this.isHealthy = isHealthy;
        this.issues = issues;
        this.stats = stats;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Геттеры
    public boolean isHealthy() { return isHealthy; }
    public List<String> getIssues() { return issues; }
    public ScalabilityStats getStats() { return stats; }
    public long getTimestamp() { return timestamp; }
    
    /**
     * Получает уровень критичности проблем
     */
    public HealthLevel getHealthLevel() {
        if (isHealthy) return HealthLevel.HEALTHY;
        
        double scalabilityScore = stats.getScalabilityScore();
        
        if (scalabilityScore >= 80.0) return HealthLevel.WARNING;
        if (scalabilityScore >= 60.0) return HealthLevel.DEGRADED;
        return HealthLevel.CRITICAL;
    }
    
    /**
     * Получает количество проблем
     */
    public int getIssueCount() {
        return issues.size();
    }
    
    /**
     * Получает краткое описание состояния
     */
    public String getStatusSummary() {
        if (isHealthy) {
            return "System is healthy - all components operating normally";
        } else {
            return String.format("System has %d issue(s) - %s level", 
                               getIssueCount(), getHealthLevel());
        }
    }
    
    /**
     * Получает рекомендации по улучшению
     */
    public List<String> getRecommendations() {
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (!isHealthy) {
            // Анализируем проблемы и даем рекомендации
            for (String issue : issues) {
                if (issue.contains("replication")) {
                    recommendations.add("Consider increasing replication factor or checking network connectivity");
                } else if (issue.contains("availability")) {
                    recommendations.add("Investigate node failures and consider adding more nodes to the network");
                } else if (issue.contains("load balancing")) {
                    recommendations.add("Review load balancing configuration and node capacity");
                } else if (issue.contains("recovery")) {
                    recommendations.add("Check data recovery processes and backup strategies");
                }
            }
            
            // Общие рекомендации
            if (stats.getScalabilityScore() < 70.0) {
                recommendations.add("Consider scaling up the network with additional nodes");
            }
            
            if (stats.getAverageLatency() > 1000.0) {
                recommendations.add("Optimize network configuration to reduce latency");
            }
        }
        
        return recommendations;
    }
    
    @Override
    public String toString() {
        StringBuilder report = new StringBuilder();
        report.append("=== System Health Report ===\n");
        report.append("Status: ").append(getStatusSummary()).append("\n");
        report.append("Health Level: ").append(getHealthLevel()).append("\n");
        report.append("Scalability Score: ").append(String.format("%.1f/100", stats.getScalabilityScore())).append("\n");
        report.append("Resilience Level: ").append(stats.getResilienceLevel()).append("\n");
        
        if (!issues.isEmpty()) {
            report.append("\nIssues Found:\n");
            for (int i = 0; i < issues.size(); i++) {
                report.append("  ").append(i + 1).append(". ").append(issues.get(i)).append("\n");
            }
        }
        
        List<String> recommendations = getRecommendations();
        if (!recommendations.isEmpty()) {
            report.append("\nRecommendations:\n");
            for (int i = 0; i < recommendations.size(); i++) {
                report.append("  ").append(i + 1).append(". ").append(recommendations.get(i)).append("\n");
            }
        }
        
        report.append("\nDetailed Stats:\n");
        report.append("  ").append(stats.toString()).append("\n");
        
        return report.toString();
    }
    
    /**
     * Уровни здоровья системы
     */
    public enum HealthLevel {
        HEALTHY("Healthy", "System is operating normally"),
        WARNING("Warning", "Minor issues detected, monitoring recommended"),
        DEGRADED("Degraded", "Performance issues detected, intervention may be needed"),
        CRITICAL("Critical", "Serious issues detected, immediate attention required");
        
        private final String name;
        private final String description;
        
        HealthLevel(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() { return name; }
    }
}

