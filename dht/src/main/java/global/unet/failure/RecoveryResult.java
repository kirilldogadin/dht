package global.unet.failure;

import global.unet.domain.structures.NodeInfo;

import java.util.List;
import java.util.Map;

/**
 * Результат операции восстановления данных
 */
public class RecoveryResult {
    
    private final boolean success;
    private final int recoveredKeys;
    private final int lostKeys;
    private final List<String> recoveredKeysList;
    private final List<String> lostKeysList;
    private final Map<NodeInfo, Integer> nodeContributions; // узел -> количество восстановленных ключей
    private final long recoveryTimeMs;
    private final String errorMessage;
    
    public RecoveryResult(boolean success, int recoveredKeys, int lostKeys,
                         List<String> recoveredKeysList, List<String> lostKeysList,
                         Map<NodeInfo, Integer> nodeContributions,
                         long recoveryTimeMs, String errorMessage) {
        this.success = success;
        this.recoveredKeys = recoveredKeys;
        this.lostKeys = lostKeys;
        this.recoveredKeysList = recoveredKeysList;
        this.lostKeysList = lostKeysList;
        this.nodeContributions = nodeContributions;
        this.recoveryTimeMs = recoveryTimeMs;
        this.errorMessage = errorMessage;
    }
    
    // Статические методы для создания результатов
    public static RecoveryResult success(int recoveredKeys, int lostKeys,
                                       List<String> recoveredKeysList, List<String> lostKeysList,
                                       Map<NodeInfo, Integer> nodeContributions, long recoveryTimeMs) {
        return new RecoveryResult(true, recoveredKeys, lostKeys, recoveredKeysList, lostKeysList,
                                nodeContributions, recoveryTimeMs, null);
    }
    
    public static RecoveryResult failure(String errorMessage, long recoveryTimeMs) {
        return new RecoveryResult(false, 0, 0, null, null, null, recoveryTimeMs, errorMessage);
    }
    
    // Геттеры
    public boolean isSuccess() { return success; }
    public int getRecoveredKeys() { return recoveredKeys; }
    public int getLostKeys() { return lostKeys; }
    public List<String> getRecoveredKeysList() { return recoveredKeysList; }
    public List<String> getLostKeysList() { return lostKeysList; }
    public Map<NodeInfo, Integer> getNodeContributions() { return nodeContributions; }
    public long getRecoveryTimeMs() { return recoveryTimeMs; }
    public String getErrorMessage() { return errorMessage; }
    
    /**
     * Получает процент успешности восстановления
     */
    public double getRecoveryRate() {
        int totalKeys = recoveredKeys + lostKeys;
        if (totalKeys == 0) return 100.0;
        return (double) recoveredKeys / totalKeys * 100.0;
    }
    
    @Override
    public String toString() {
        if (success) {
            return String.format("RecoveryResult{recovered=%d, lost=%d (%.1f%%), time=%dms}",
                               recoveredKeys, lostKeys, getRecoveryRate(), recoveryTimeMs);
        } else {
            return String.format("RecoveryResult{failed: %s, time=%dms}", errorMessage, recoveryTimeMs);
        }
    }
}

