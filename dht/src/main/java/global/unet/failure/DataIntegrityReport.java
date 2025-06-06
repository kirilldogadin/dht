package global.unet.failure;

import java.util.List;
import java.util.Map;

/**
 * Отчет о целостности данных в DHT сети
 */
public class DataIntegrityReport {
    
    private final boolean integrityOk;
    private final long totalKeys;
    private final long keysWithSufficientReplicas;
    private final long keysWithInsufficientReplicas;
    private final long inconsistentKeys;
    private final long orphanedKeys;
    private final Map<String, Integer> replicationFactorDistribution; // фактор -> количество ключей
    private final List<String> problematicKeys;
    private final long checkTimeMs;
    private final String summary;
    
    public DataIntegrityReport(boolean integrityOk, long totalKeys, long keysWithSufficientReplicas,
                             long keysWithInsufficientReplicas, long inconsistentKeys, long orphanedKeys,
                             Map<String, Integer> replicationFactorDistribution, List<String> problematicKeys,
                             long checkTimeMs, String summary) {
        this.integrityOk = integrityOk;
        this.totalKeys = totalKeys;
        this.keysWithSufficientReplicas = keysWithSufficientReplicas;
        this.keysWithInsufficientReplicas = keysWithInsufficientReplicas;
        this.inconsistentKeys = inconsistentKeys;
        this.orphanedKeys = orphanedKeys;
        this.replicationFactorDistribution = replicationFactorDistribution;
        this.problematicKeys = problematicKeys;
        this.checkTimeMs = checkTimeMs;
        this.summary = summary;
    }
    
    // Геттеры
    public boolean isIntegrityOk() { return integrityOk; }
    public long getTotalKeys() { return totalKeys; }
    public long getKeysWithSufficientReplicas() { return keysWithSufficientReplicas; }
    public long getKeysWithInsufficientReplicas() { return keysWithInsufficientReplicas; }
    public long getInconsistentKeys() { return inconsistentKeys; }
    public long getOrphanedKeys() { return orphanedKeys; }
    public Map<String, Integer> getReplicationFactorDistribution() { return replicationFactorDistribution; }
    public List<String> getProblematicKeys() { return problematicKeys; }
    public long getCheckTimeMs() { return checkTimeMs; }
    public String getSummary() { return summary; }
    
    /**
     * Получает процент ключей с достаточной репликацией
     */
    public double getSufficientReplicationRate() {
        if (totalKeys == 0) return 100.0;
        return (double) keysWithSufficientReplicas / totalKeys * 100.0;
    }
    
    /**
     * Получает процент консистентных ключей
     */
    public double getConsistencyRate() {
        if (totalKeys == 0) return 100.0;
        return (double) (totalKeys - inconsistentKeys) / totalKeys * 100.0;
    }
    
    /**
     * Получает общий балл целостности (0-100)
     */
    public double getIntegrityScore() {
        if (totalKeys == 0) return 100.0;
        
        double replicationScore = getSufficientReplicationRate();
        double consistencyScore = getConsistencyRate();
        double orphanPenalty = (double) orphanedKeys / totalKeys * 100.0;
        
        return Math.max(0, (replicationScore + consistencyScore) / 2.0 - orphanPenalty);
    }
    
    @Override
    public String toString() {
        return String.format(
            "DataIntegrityReport{integrity=%s, keys=%d, sufficient=%.1f%%, consistent=%.1f%%, score=%.1f}",
            integrityOk ? "OK" : "ISSUES", totalKeys, getSufficientReplicationRate(),
            getConsistencyRate(), getIntegrityScore()
        );
    }
}

