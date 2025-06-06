package global.unet.replication;

import global.unet.domain.structures.NodeInfo;

import java.util.List;
import java.util.Map;

/**
 * Результат операции репликации
 */
public class ReplicationResult {
    
    private final boolean success;
    private final int successfulReplicas;
    private final int totalReplicas;
    private final List<NodeInfo> successfulNodes;
    private final Map<NodeInfo, String> failedNodes; // узел -> причина ошибки
    private final long replicationTimeMs;
    private final String errorMessage;
    
    public ReplicationResult(boolean success, int successfulReplicas, int totalReplicas,
                           List<NodeInfo> successfulNodes, Map<NodeInfo, String> failedNodes,
                           long replicationTimeMs, String errorMessage) {
        this.success = success;
        this.successfulReplicas = successfulReplicas;
        this.totalReplicas = totalReplicas;
        this.successfulNodes = successfulNodes;
        this.failedNodes = failedNodes;
        this.replicationTimeMs = replicationTimeMs;
        this.errorMessage = errorMessage;
    }
    
    // Статические методы для создания результатов
    public static ReplicationResult success(int successfulReplicas, int totalReplicas,
                                          List<NodeInfo> successfulNodes, 
                                          Map<NodeInfo, String> failedNodes,
                                          long replicationTimeMs) {
        return new ReplicationResult(true, successfulReplicas, totalReplicas,
                                   successfulNodes, failedNodes, replicationTimeMs, null);
    }
    
    public static ReplicationResult failure(String errorMessage, long replicationTimeMs) {
        return new ReplicationResult(false, 0, 0, null, null, replicationTimeMs, errorMessage);
    }
    
    // Геттеры
    public boolean isSuccess() { return success; }
    public int getSuccessfulReplicas() { return successfulReplicas; }
    public int getTotalReplicas() { return totalReplicas; }
    public List<NodeInfo> getSuccessfulNodes() { return successfulNodes; }
    public Map<NodeInfo, String> getFailedNodes() { return failedNodes; }
    public long getReplicationTimeMs() { return replicationTimeMs; }
    public String getErrorMessage() { return errorMessage; }
    
    /**
     * Получает процент успешности репликации
     */
    public double getSuccessRate() {
        if (totalReplicas == 0) return 0.0;
        return (double) successfulReplicas / totalReplicas * 100.0;
    }
    
    /**
     * Проверяет, достигнут ли кворум (большинство реплик)
     */
    public boolean hasQuorum() {
        return successfulReplicas > totalReplicas / 2;
    }
    
    @Override
    public String toString() {
        if (success) {
            return String.format("ReplicationResult{success=%d/%d (%.1f%%), time=%dms}",
                               successfulReplicas, totalReplicas, getSuccessRate(), replicationTimeMs);
        } else {
            return String.format("ReplicationResult{failed: %s, time=%dms}", errorMessage, replicationTimeMs);
        }
    }
}

