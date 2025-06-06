package global.unet.loadbalancing;

import global.unet.domain.structures.NodeInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс для балансировки нагрузки в DHT сети
 */
public interface LoadBalancer {
    
    /**
     * Запускает балансировщик нагрузки
     */
    void start();
    
    /**
     * Останавливает балансировщик нагрузки
     */
    void stop();
    
    /**
     * Выбирает оптимальный узел для выполнения операции
     * 
     * @param operation тип операции
     * @param key ключ (может быть null для некоторых операций)
     * @param candidateNodes список кандидатов
     * @return выбранный узел
     */
    NodeInfo selectNode(OperationType operation, String key, List<NodeInfo> candidateNodes);
    
    /**
     * Выбирает несколько узлов для распределенной операции
     * 
     * @param operation тип операции
     * @param key ключ
     * @param candidateNodes список кандидатов
     * @param count количество узлов
     * @return список выбранных узлов
     */
    List<NodeInfo> selectNodes(OperationType operation, String key, List<NodeInfo> candidateNodes, int count);
    
    /**
     * Обновляет статистику нагрузки узла
     * 
     * @param node узел
     * @param loadMetrics метрики нагрузки
     */
    void updateNodeLoad(NodeInfo node, NodeLoadMetrics loadMetrics);
    
    /**
     * Получает текущую нагрузку узла
     * 
     * @param node узел
     * @return метрики нагрузки
     */
    NodeLoadMetrics getNodeLoad(NodeInfo node);
    
    /**
     * Получает статистику балансировки нагрузки
     * 
     * @return статистика балансировки
     */
    LoadBalancingStats getStats();
    
    /**
     * Настраивает параметры балансировки
     * 
     * @param config конфигурация балансировки
     */
    void configure(LoadBalancingConfig config);
    
    /**
     * Проверяет, перегружен ли узел
     * 
     * @param node узел для проверки
     * @return true если узел перегружен
     */
    boolean isNodeOverloaded(NodeInfo node);
    
    /**
     * Получает список перегруженных узлов
     * 
     * @return список перегруженных узлов
     */
    List<NodeInfo> getOverloadedNodes();
    
    /**
     * Получает список недогруженных узлов
     * 
     * @return список недогруженных узлов
     */
    List<NodeInfo> getUnderloadedNodes();
}

