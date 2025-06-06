package global.unet.failure;

import global.unet.domain.structures.NodeInfo;

/**
 * Интерфейс для слушателя событий отказов узлов
 */
public interface FailureListener {
    
    /**
     * Вызывается при обнаружении отказа узла
     * 
     * @param node отказавший узел
     * @param reason причина отказа
     */
    void onNodeFailure(NodeInfo node, String reason);
    
    /**
     * Вызывается при восстановлении узла после отказа
     * 
     * @param node восстановленный узел
     */
    void onNodeRecovery(NodeInfo node);
    
    /**
     * Вызывается при обнаружении разделения сети
     * 
     * @param partitionId идентификатор раздела сети
     * @param affectedNodes затронутые узлы
     */
    void onNetworkPartition(String partitionId, NodeInfo[] affectedNodes);
    
    /**
     * Вызывается при восстановлении сети после разделения
     * 
     * @param partitionId идентификатор раздела сети
     */
    void onPartitionHealed(String partitionId);
}

