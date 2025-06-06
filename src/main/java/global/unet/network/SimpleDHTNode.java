package global.unet.network;

import global.unet.domain.id.KademliaId;
import global.unet.domain.structures.Bucket;
import global.unet.domain.structures.NodeInfo;
import global.unet.domain.node.NodeType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Простая реализация DHT узла на основе протокола Kademlia
 * Базовая версия для демонстрации основных принципов работы
 */
public class SimpleDHTNode {
    private static final Logger logger = Logger.getLogger(SimpleDHTNode.class.getName());
    
    // Основные параметры Kademlia
    private static final int K = 20; // Размер k-bucket
    private static final int ALPHA = 3; // Параллелизм поиска
    private static final int ID_LENGTH = 160; // Длина ID в битах
    
    // Идентификатор и информация о узле
    private final KademliaId nodeId;
    private final NodeInfo nodeInfo;
    private final int port;
    
    // Таблица маршрутизации (k-buckets)
    private final List<Bucket> routingTable;
    
    // Локальное хранилище данных
    private final Map<String, Object> localStorage;
    
    // Планировщик для периодических задач
    private final ScheduledExecutorService scheduler;
    
    // Статистика узла
    private long startTime;
    private int totalRequests;
    private int successfulRequests;
    
    /**
     * Конструктор DHT узла
     */
    public SimpleDHTNode(int port) {
        this.port = port;
        this.nodeId = KademliaId.random();
        this.nodeInfo = new NodeInfo(nodeId, "localhost", port, NodeType.REGULAR);
        this.routingTable = initializeRoutingTable();
        this.localStorage = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.startTime = System.currentTimeMillis();
        this.totalRequests = 0;
        this.successfulRequests = 0;
        
        logger.info("DHT узел создан: " + nodeId + " на порту " + port);
    }
    
    /**
     * Инициализация таблицы маршрутизации
     */
    private List<Bucket> initializeRoutingTable() {
        List<Bucket> table = new ArrayList<>();
        for (int i = 0; i < ID_LENGTH; i++) {
            table.add(new Bucket(K));
        }
        return table;
    }
    
    /**
     * Запуск DHT узла
     */
    public void start() {
        logger.info("Запуск DHT узла " + nodeId);
        
        // Запуск периодических задач
        startPeriodicTasks();
        
        // Инициализация сетевого слоя
        initializeNetworking();
        
        logger.info("DHT узел " + nodeId + " успешно запущен");
    }
    
    /**
     * Остановка DHT узла
     */
    public void stop() {
        logger.info("Остановка DHT узла " + nodeId);
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("DHT узел " + nodeId + " остановлен");
    }
    
    /**
     * Сохранение данных в DHT
     */
    public boolean store(String key, Object value) {
        totalRequests++;
        try {
            KademliaId keyId = KademliaId.fromString(key);
            
            // Находим ближайшие узлы для ключа
            List<NodeInfo> closestNodes = findClosestNodes(keyId, K);
            
            if (closestNodes.isEmpty()) {
                // Если нет других узлов, сохраняем локально
                localStorage.put(key, value);
                successfulRequests++;
                logger.info("Данные сохранены локально: " + key);
                return true;
            }
            
            // Отправляем данные на ближайшие узлы
            boolean success = false;
            for (NodeInfo node : closestNodes) {
                if (sendStoreRequest(node, key, value)) {
                    success = true;
                }
            }
            
            // Также сохраняем локально если мы среди ближайших
            if (isAmongClosest(keyId, closestNodes)) {
                localStorage.put(key, value);
                success = true;
            }
            
            if (success) {
                successfulRequests++;
                logger.info("Данные успешно сохранены: " + key);
            }
            
            return success;
            
        } catch (Exception e) {
            logger.severe("Ошибка сохранения данных: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Поиск данных в DHT
     */
    public Object find(String key) {
        totalRequests++;
        try {
            // Сначала проверяем локальное хранилище
            if (localStorage.containsKey(key)) {
                successfulRequests++;
                logger.info("Данные найдены локально: " + key);
                return localStorage.get(key);
            }
            
            KademliaId keyId = KademliaId.fromString(key);
            
            // Ищем на ближайших узлах
            List<NodeInfo> closestNodes = findClosestNodes(keyId, K);
            
            for (NodeInfo node : closestNodes) {
                Object result = sendFindRequest(node, key);
                if (result != null) {
                    successfulRequests++;
                    logger.info("Данные найдены на узле " + node.getId() + ": " + key);
                    return result;
                }
            }
            
            logger.info("Данные не найдены: " + key);
            return null;
            
        } catch (Exception e) {
            logger.severe("Ошибка поиска данных: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Поиск ближайших узлов к заданному ID
     */
    public List<NodeInfo> findClosestNodes(KademliaId targetId, int count) {
        List<NodeInfo> allNodes = new ArrayList<>();
        
        // Собираем все узлы из таблицы маршрутизации
        for (Bucket bucket : routingTable) {
            allNodes.addAll(bucket.getNodes());
        }
        
        // Сортируем по расстоянию до целевого ID
        allNodes.sort((a, b) -> {
            KademliaId distA = a.getId().xor(targetId);
            KademliaId distB = b.getId().xor(targetId);
            return distA.compareTo(distB);
        });
        
        // Возвращаем первые count узлов
        return allNodes.subList(0, Math.min(count, allNodes.size()));
    }
    
    /**
     * Добавление узла в таблицу маршрутизации
     */
    public void addNode(NodeInfo node) {
        if (node.getId().equals(nodeId)) {
            return; // Не добавляем себя
        }
        
        int bucketIndex = getBucketIndex(node.getId());
        Bucket bucket = routingTable.get(bucketIndex);
        
        if (bucket.addNode(node)) {
            logger.info("Узел добавлен в таблицу маршрутизации: " + node.getId());
        }
    }
    
    /**
     * Получение индекса bucket для узла
     */
    private int getBucketIndex(KademliaId nodeId) {
        KademliaId distance = this.nodeId.xor(nodeId);
        return Math.max(0, ID_LENGTH - distance.getLeadingZeros() - 1);
    }
    
    /**
     * Проверка, находимся ли мы среди ближайших узлов
     */
    private boolean isAmongClosest(KademliaId keyId, List<NodeInfo> closestNodes) {
        KademliaId ourDistance = nodeId.xor(keyId);
        
        for (NodeInfo node : closestNodes) {
            KademliaId nodeDistance = node.getId().xor(keyId);
            if (ourDistance.compareTo(nodeDistance) <= 0) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Отправка запроса на сохранение данных
     */
    private boolean sendStoreRequest(NodeInfo node, String key, Object value) {
        // Заглушка для сетевого взаимодействия
        // В реальной реализации здесь был бы HTTP/TCP запрос
        logger.info("Отправка STORE запроса на узел " + node.getId() + " для ключа " + key);
        return true;
    }
    
    /**
     * Отправка запроса на поиск данных
     */
    private Object sendFindRequest(NodeInfo node, String key) {
        // Заглушка для сетевого взаимодействия
        // В реальной реализации здесь был бы HTTP/TCP запрос
        logger.info("Отправка FIND запроса на узел " + node.getId() + " для ключа " + key);
        return null;
    }
    
    /**
     * Инициализация сетевого слоя
     */
    private void initializeNetworking() {
        // Заглушка для инициализации сетевого слоя
        // В реальной реализации здесь был бы HTTP сервер или TCP сокет
        logger.info("Сетевой слой инициализирован на порту " + port);
    }
    
    /**
     * Запуск периодических задач
     */
    private void startPeriodicTasks() {
        // Периодическое обновление таблицы маршрутизации
        scheduler.scheduleAtFixedRate(this::refreshRoutingTable, 60, 60, TimeUnit.SECONDS);
        
        // Периодическая проверка состояния узлов
        scheduler.scheduleAtFixedRate(this::checkNodeHealth, 30, 30, TimeUnit.SECONDS);
        
        // Периодический вывод статистики
        scheduler.scheduleAtFixedRate(this::logStatistics, 300, 300, TimeUnit.SECONDS);
    }
    
    /**
     * Обновление таблицы маршрутизации
     */
    private void refreshRoutingTable() {
        logger.info("Обновление таблицы маршрутизации...");
        // Логика обновления таблицы маршрутизации
    }
    
    /**
     * Проверка состояния узлов
     */
    private void checkNodeHealth() {
        logger.info("Проверка состояния узлов...");
        // Логика проверки доступности узлов
    }
    
    /**
     * Вывод статистики
     */
    private void logStatistics() {
        long uptime = System.currentTimeMillis() - startTime;
        double successRate = totalRequests > 0 ? (double) successfulRequests / totalRequests * 100 : 0;
        
        logger.info(String.format(
            "Статистика узла %s: Время работы: %d сек, Запросов: %d, Успешных: %d (%.1f%%)",
            nodeId, uptime / 1000, totalRequests, successfulRequests, successRate
        ));
    }
    
    // Геттеры
    public KademliaId getNodeId() { return nodeId; }
    public NodeInfo getNodeInfo() { return nodeInfo; }
    public int getPort() { return port; }
    public Map<String, Object> getLocalStorage() { return new HashMap<>(localStorage); }
    public int getTotalRequests() { return totalRequests; }
    public int getSuccessfulRequests() { return successfulRequests; }
    public long getUptime() { return System.currentTimeMillis() - startTime; }
}

