package global.unet.network;

import global.unet.replication.ReplicationConfig;
import global.unet.failure.FailureDetectionConfig;
import global.unet.failure.RecoveryConfig;
import global.unet.loadbalancing.LoadBalancingConfig;

/**
 * Конфигурация для масштабируемого DHT узла
 */
public class ScalabilityConfig {
    
    private final int replicationFactor;
    private final ReplicationConfig replicationConfig;
    private final FailureDetectionConfig failureDetectionConfig;
    private final RecoveryConfig recoveryConfig;
    private final LoadBalancingConfig loadBalancingConfig;
    
    public ScalabilityConfig(int replicationFactor,
                           ReplicationConfig replicationConfig,
                           FailureDetectionConfig failureDetectionConfig,
                           RecoveryConfig recoveryConfig,
                           LoadBalancingConfig loadBalancingConfig) {
        this.replicationFactor = Math.max(1, replicationFactor);
        this.replicationConfig = replicationConfig;
        this.failureDetectionConfig = failureDetectionConfig;
        this.recoveryConfig = recoveryConfig;
        this.loadBalancingConfig = loadBalancingConfig;
    }
    
    // Конструктор с настройками по умолчанию
    public ScalabilityConfig() {
        this(3, 
             new ReplicationConfig(),
             new FailureDetectionConfig(),
             new RecoveryConfig(),
             new LoadBalancingConfig());
    }
    
    // Геттеры
    public int getReplicationFactor() { return replicationFactor; }
    public ReplicationConfig getReplicationConfig() { return replicationConfig; }
    public FailureDetectionConfig getFailureDetectionConfig() { return failureDetectionConfig; }
    public RecoveryConfig getRecoveryConfig() { return recoveryConfig; }
    public LoadBalancingConfig getLoadBalancingConfig() { return loadBalancingConfig; }
    
    @Override
    public String toString() {
        return String.format(
            "ScalabilityConfig{replicationFactor=%d, replication=%s, failure=%s, recovery=%s, loadBalancing=%s}",
            replicationFactor, replicationConfig, failureDetectionConfig, recoveryConfig, loadBalancingConfig
        );
    }
}

