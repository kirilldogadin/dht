package global.unet.replication;

import global.unet.domain.structures.NodeInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс для стратегий репликации данных
 */
public interface ReplicationStrategy {
    
    /**
     * Выполняет репликацию данных на указанные узлы
     * 
     * @param key ключ для репликации
     * @param value значение для репликации
     * @param targetNodes узлы для репликации
     * @return Future с результатом репликации
     */
    CompletableFuture<ReplicationResult> replicate(String key, String value, List<NodeInfo> targetNodes);
    
    /**
     * Получает название стратегии
     * 
     * @return название стратегии
     */
    String getStrategyName();
    
    /**
     * Получает описание стратегии
     * 
     * @return описание стратегии
     */
    String getDescription();
    
    /**
     * Проверяет, поддерживает ли стратегия асинхронную репликацию
     * 
     * @return true если поддерживает асинхронную репликацию
     */
    boolean isAsynchronous();
}

