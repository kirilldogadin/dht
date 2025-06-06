package global.unet.failure;

import global.unet.domain.structures.NodeInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс для детекции отказов узлов в DHT сети
 */
public interface FailureDetector {
    
    /**
     * Запускает мониторинг узлов
     */
    void start();
    
    /**
     * Останавливает мониторинг узлов
     */
    void stop();
    
    /**
     * Добавляет узел для мониторинга
     * 
     * @param node узел для мониторинга
     */
    void addNode(NodeInfo node);
    
    /**
     * Удаляет узел из мониторинга
     * 
     * @param node узел для удаления
     */
    void removeNode(NodeInfo node);
    
    /**
     * Проверяет доступность узла
     * 
     * @param node узел для проверки
     * @return Future с результатом проверки
     */
    CompletableFuture<Boolean> checkNode(NodeInfo node);
    
    /**
     * Получает список недоступных узлов
     * 
     * @return список недоступных узлов
     */
    List<NodeInfo> getFailedNodes();
    
    /**
     * Получает список доступных узлов
     * 
     * @return список доступных узлов
     */
    List<NodeInfo> getHealthyNodes();
    
    /**
     * Регистрирует слушателя событий отказов
     * 
     * @param listener слушатель событий
     */
    void addFailureListener(FailureListener listener);
    
    /**
     * Удаляет слушателя событий отказов
     * 
     * @param listener слушатель событий
     */
    void removeFailureListener(FailureListener listener);
    
    /**
     * Получает статистику детекции отказов
     * 
     * @return статистика детекции отказов
     */
    FailureDetectionStats getStats();
    
    /**
     * Настраивает параметры детекции отказов
     * 
     * @param config конфигурация детекции отказов
     */
    void configure(FailureDetectionConfig config);
}

