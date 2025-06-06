package global.unet.failure;

import global.unet.domain.structures.NodeInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс для управления восстановлением после отказов узлов
 */
public interface RecoveryManager {
    
    /**
     * Запускает менеджер восстановления
     */
    void start();
    
    /**
     * Останавливает менеджер восстановления
     */
    void stop();
    
    /**
     * Обрабатывает отказ узла
     * 
     * @param failedNode отказавший узел
     * @return Future с результатом обработки
     */
    CompletableFuture<Boolean> handleNodeFailure(NodeInfo failedNode);
    
    /**
     * Обрабатывает восстановление узла
     * 
     * @param recoveredNode восстановленный узел
     * @return Future с результатом обработки
     */
    CompletableFuture<Boolean> handleNodeRecovery(NodeInfo recoveredNode);
    
    /**
     * Восстанавливает данные с отказавшего узла
     * 
     * @param failedNode отказавший узел
     * @param replacementNodes узлы для замещения
     * @return Future с результатом восстановления
     */
    CompletableFuture<RecoveryResult> recoverData(NodeInfo failedNode, List<NodeInfo> replacementNodes);
    
    /**
     * Перебалансирует данные после изменения топологии
     * 
     * @return Future с результатом перебалансировки
     */
    CompletableFuture<Boolean> rebalanceData();
    
    /**
     * Проверяет целостность данных в сети
     * 
     * @return Future с результатом проверки
     */
    CompletableFuture<DataIntegrityReport> checkDataIntegrity();
    
    /**
     * Получает статистику восстановления
     * 
     * @return статистика восстановления
     */
    RecoveryStats getRecoveryStats();
    
    /**
     * Настраивает параметры восстановления
     * 
     * @param config конфигурация восстановления
     */
    void configure(RecoveryConfig config);
}

