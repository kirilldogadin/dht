package global.unet.replication;

import global.unet.domain.id.KademliaId;
import global.unet.domain.structures.NodeInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс для управления репликацией данных в DHT сети
 */
public interface ReplicationManager {
    
    /**
     * Сохраняет данные с репликацией на несколько узлов
     * 
     * @param key ключ для сохранения
     * @param value значение для сохранения
     * @param replicationFactor количество реплик
     * @return Future с результатом операции
     */
    CompletableFuture<Boolean> store(String key, String value, int replicationFactor);
    
    /**
     * Получает данные с учетом репликации
     * 
     * @param key ключ для поиска
     * @return Future со значением или null если не найдено
     */
    CompletableFuture<String> retrieve(String key);
    
    /**
     * Удаляет данные со всех реплик
     * 
     * @param key ключ для удаления
     * @return Future с результатом операции
     */
    CompletableFuture<Boolean> delete(String key);
    
    /**
     * Получает список узлов, ответственных за хранение ключа
     * 
     * @param key ключ
     * @param replicationFactor количество реплик
     * @return список узлов для репликации
     */
    List<NodeInfo> getReplicationNodes(String key, int replicationFactor);
    
    /**
     * Синхронизирует реплики для указанного ключа
     * 
     * @param key ключ для синхронизации
     * @return Future с результатом синхронизации
     */
    CompletableFuture<Boolean> synchronizeReplicas(String key);
    
    /**
     * Проверяет консистентность реплик
     * 
     * @param key ключ для проверки
     * @return Future с результатом проверки консистентности
     */
    CompletableFuture<Boolean> checkConsistency(String key);
    
    /**
     * Восстанавливает недостающие реплики
     * 
     * @param key ключ для восстановления
     * @param targetReplicationFactor целевое количество реплик
     * @return Future с результатом восстановления
     */
    CompletableFuture<Boolean> repairReplicas(String key, int targetReplicationFactor);
    
    /**
     * Получает статистику репликации
     * 
     * @return статистика репликации
     */
    ReplicationStats getReplicationStats();
}

