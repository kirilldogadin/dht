package global.unet.domain.structures;

import global.unet.domain.id.KademliaId;
import global.unet.domain.id.UnionId;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * K-bucket с LRU логикой и механизмами обновления, пинга и разделения
 */
public class Bucket {
    // Блокировка для обеспечения потокобезопасности
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Используем ArrayList вместо HashSet для поддержки LRU логики
    private final List<NodeInfo> nodeInfoList;
    private final int maxCapacity;
    private final int kResponsibility;
    
    // Планировщик для выполнения периодических задач (пинг неактивных узлов)
    private final ScheduledExecutorService scheduler;
    
    // Время неактивности, после которого узел считается неактивным (в минутах)
    private static final long INACTIVE_THRESHOLD_MINUTES = 30;
    
    // Максимальное количество неудачных попыток связи перед удалением узла
    private static final int MAX_FAILED_ATTEMPTS = 3;
    
    // Колбэк для пинга узла
    private Consumer<NodeInfo> pingCallback;
    
    // Родительский бакет (для разделения)
    private Bucket parentBucket;
    
    // Дочерние бакеты (после разделения)
    private Bucket leftChild;
    private Bucket rightChild;
    
    // Префикс, за который отвечает этот бакет
    private KademliaId prefix;
    private int prefixLength;

    /**
     * Создает новый K-bucket с указанной емкостью и ответственностью
     * 
     * @param maxCapacity максимальная емкость бакета
     * @param kResponsibility количество бит ответственности
     */
    public Bucket(int maxCapacity, int kResponsibility) {
        this.maxCapacity = maxCapacity;
        this.nodeInfoList = new ArrayList<>(maxCapacity);
        this.kResponsibility = kResponsibility;
        this.scheduler = new ScheduledThreadPoolExecutor(1);
        this.prefixLength = 0;
        
        // Запускаем периодическую задачу для проверки неактивных узлов
        scheduler.scheduleAtFixedRate(this::checkInactiveNodes, 5, 5, TimeUnit.MINUTES);
    }
    
    /**
     * Создает новый K-bucket с указанной емкостью, ответственностью и префиксом
     * 
     * @param maxCapacity максимальная емкость бакета
     * @param kResponsibility количество бит ответственности
     * @param prefix префикс, за который отвечает этот бакет
     * @param prefixLength длина префикса в битах
     */
    public Bucket(int maxCapacity, int kResponsibility, KademliaId prefix, int prefixLength) {
        this(maxCapacity, kResponsibility);
        this.prefix = prefix;
        this.prefixLength = prefixLength;
    }
    
    /**
     * Устанавливает колбэк для пинга узла
     * 
     * @param pingCallback функция для пинга узла
     */
    public void setPingCallback(Consumer<NodeInfo> pingCallback) {
        this.pingCallback = pingCallback;
    }

    /**
     * Получить список узлов в бакете
     * @return копию списка узлов для безопасного доступа
     */
    public List<NodeInfo> getNodeInfoList() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(nodeInfoList);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Добавить узел в бакет с учетом LRU логики
     * @param nodeInfo информация о узле
     * @return true если узел был добавлен, false если узел уже существует и был обновлен
     */
    public boolean add(NodeInfo nodeInfo) {
        lock.writeLock().lock();
        try {
            // Проверяем, существует ли уже узел в бакете
            int existingIndex = findNodeIndex(nodeInfo);
            
            if (existingIndex >= 0) {
                // Узел уже существует, перемещаем его в конец списка (LRU логика)
                NodeInfo existing = nodeInfoList.remove(existingIndex);
                existing.updateLastSeen(); // Обновляем время последней активности
                nodeInfoList.add(existing);
                return false;
            } else {
                // Проверяем, нужно ли освободить место
                if (nodeInfoList.size() >= maxCapacity) {
                    // Если бакет разделен, делегируем добавление дочерним бакетам
                    if (isSubdivided()) {
                        UnionId nodeId = nodeInfo.getUnionId();
                        if (nodeId instanceof KademliaId) {
                            KademliaId kadId = (KademliaId) nodeId;
                            // Определяем, в какой дочерний бакет добавить узел
                            if (shouldGoLeft(kadId)) {
                                return leftChild.add(nodeInfo);
                            } else {
                                return rightChild.add(nodeInfo);
                            }
                        }
                    } else {
                        // Пытаемся разделить бакет, если это возможно
                        if (canSubdivide()) {
                            subdivide();
                            return add(nodeInfo); // Рекурсивно добавляем узел после разделения
                        } else {
                            // Если разделение невозможно, применяем LRU логику
                            handleCapacityExceeded();
                        }
                    }
                }
                
                // Добавляем новый узел в конец списка
                nodeInfoList.add(nodeInfo);
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Найти индекс узла в списке
     * @param nodeInfo информация о узле
     * @return индекс узла или -1, если узел не найден
     */
    private int findNodeIndex(NodeInfo nodeInfo) {
        for (int i = 0; i < nodeInfoList.size(); i++) {
            if (nodeInfoList.get(i).equals(nodeInfo)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Обработка ситуации, когда превышена емкость бакета
     * Реализует LRU (Least Recently Used) логику и механизм пинга неактивных узлов
     */
    private void handleCapacityExceeded() {
        // Сначала ищем неактивные узлы для удаления
        for (int i = 0; i < nodeInfoList.size(); i++) {
            NodeInfo node = nodeInfoList.get(i);
            if (!node.isActive() || isNodeStale(node)) {
                nodeInfoList.remove(i);
                return;
            }
        }
        
        // Если все узлы активны, пингуем наименее недавно использованный узел
        if (pingCallback != null && !nodeInfoList.isEmpty()) {
            NodeInfo leastRecentNode = nodeInfoList.get(0);
            pingCallback.accept(leastRecentNode);
            
            // Если после пинга узел неактивен, удаляем его
            if (!leastRecentNode.isActive() || leastRecentNode.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                nodeInfoList.remove(0);
                return;
            }
        }
        
        // Если все узлы активны и пинг не помог, удаляем наименее недавно использованный узел
        if (!nodeInfoList.isEmpty()) {
            nodeInfoList.remove(0);
        }
    }
    
    /**
     * Проверяет, является ли узел устаревшим (не было активности долгое время)
     * 
     * @param node информация о узле
     * @return true, если узел устарел
     */
    private boolean isNodeStale(NodeInfo node) {
        Instant now = Instant.now();
        Duration sinceLastSeen = Duration.between(node.getLastSeen(), now);
        return sinceLastSeen.toMinutes() > INACTIVE_THRESHOLD_MINUTES;
    }
    
    /**
     * Периодически проверяет неактивные узлы и пингует их
     */
    private void checkInactiveNodes() {
        lock.writeLock().lock();
        try {
            List<NodeInfo> nodesToPing = new ArrayList<>();
            
            // Собираем список узлов, которые нужно пингануть
            for (NodeInfo node : nodeInfoList) {
                if (isNodeStale(node)) {
                    nodesToPing.add(node);
                }
            }
            
            // Отпускаем блокировку перед выполнением пингов
            lock.writeLock().unlock();
            
            // Пингуем узлы
            if (pingCallback != null) {
                for (NodeInfo node : nodesToPing) {
                    pingCallback.accept(node);
                }
            }
            
            // Снова захватываем блокировку для обновления списка
            lock.writeLock().lock();
            
            // Удаляем узлы, которые не ответили на пинг
            nodeInfoList.removeIf(node -> 
                !node.isActive() || node.getFailedAttempts() >= MAX_FAILED_ATTEMPTS);
                
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Обновить узел (пометить как недавно использованный)
     * @param nodeInfo информация о узле
     * @return true если узел был найден и обновлен, false в противном случае
     */
    public boolean update(NodeInfo nodeInfo) {
        lock.writeLock().lock();
        try {
            // Если бакет разделен, делегируем обновление дочерним бакетам
            if (isSubdivided()) {
                UnionId nodeId = nodeInfo.getUnionId();
                if (nodeId instanceof KademliaId) {
                    KademliaId kadId = (KademliaId) nodeId;
                    if (shouldGoLeft(kadId)) {
                        return leftChild.update(nodeInfo);
                    } else {
                        return rightChild.update(nodeInfo);
                    }
                }
                return false;
            }
            
            int index = findNodeIndex(nodeInfo);
            if (index >= 0) {
                // Перемещаем узел в конец списка (помечаем как недавно использованный)
                NodeInfo node = nodeInfoList.remove(index);
                node.updateLastSeen(); // Обновляем время последней активности
                nodeInfoList.add(node);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Проверить, содержит ли бакет указанный узел
     * @param nodeInfo информация о узле
     * @return true если узел найден, false в противном случае
     */
    public boolean contains(NodeInfo nodeInfo) {
        lock.readLock().lock();
        try {
            // Если бакет разделен, делегируем проверку дочерним бакетам
            if (isSubdivided()) {
                UnionId nodeId = nodeInfo.getUnionId();
                if (nodeId instanceof KademliaId) {
                    KademliaId kadId = (KademliaId) nodeId;
                    if (shouldGoLeft(kadId)) {
                        return leftChild.contains(nodeInfo);
                    } else {
                        return rightChild.contains(nodeInfo);
                    }
                }
                return false;
            }
            
            return findNodeIndex(nodeInfo) >= 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получить количество узлов в бакете
     * @return количество узлов
     */
    public int size() {
        lock.readLock().lock();
        try {
            // Если бакет разделен, возвращаем сумму размеров дочерних бакетов
            if (isSubdivided()) {
                return leftChild.size() + rightChild.size();
            }
            
            return nodeInfoList.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Проверяет, разделен ли бакет на дочерние бакеты
     * 
     * @return true, если бакет разделен
     */
    public boolean isSubdivided() {
        return leftChild != null && rightChild != null;
    }
    
    /**
     * Проверяет, может ли бакет быть разделен
     * 
     * @return true, если бакет может быть разделен
     */
    private boolean canSubdivide() {
        // Бакет может быть разделен, если его префикс меньше максимальной длины
        return prefixLength < KademliaId.BIT_COUNT - 1;
    }
    
    /**
     * Разделяет бакет на два дочерних бакета
     */
    private void subdivide() {
        if (!canSubdivide() || isSubdivided()) {
            return;
        }
        
        // Создаем два дочерних бакета
        leftChild = new Bucket(maxCapacity, kResponsibility, prefix, prefixLength + 1);
        rightChild = new Bucket(maxCapacity, kResponsibility, prefix, prefixLength + 1);
        
        // Устанавливаем колбэки для пинга
        leftChild.setPingCallback(pingCallback);
        rightChild.setPingCallback(pingCallback);
        
        // Устанавливаем родительский бакет
        leftChild.parentBucket = this;
        rightChild.parentBucket = this;
        
        // Распределяем узлы по дочерним бакетам
        List<NodeInfo> nodesToRedistribute = new ArrayList<>(nodeInfoList);
        nodeInfoList.clear(); // Очищаем текущий список
        
        for (NodeInfo node : nodesToRedistribute) {
            UnionId nodeId = node.getUnionId();
            if (nodeId instanceof KademliaId) {
                KademliaId kadId = (KademliaId) nodeId;
                if (shouldGoLeft(kadId)) {
                    leftChild.add(node);
                } else {
                    rightChild.add(node);
                }
            }
        }
    }
    
    /**
     * Определяет, должен ли узел идти в левый дочерний бакет
     * 
     * @param nodeId идентификатор узла
     * @return true, если узел должен идти в левый дочерний бакет
     */
    private boolean shouldGoLeft(KademliaId nodeId) {
        // Проверяем бит на позиции prefixLength
        // Если бит равен 0, узел идет в левый бакет, иначе в правый
        byte[] bytes = nodeId.asBytes();
        int byteIndex = prefixLength / 8;
        int bitIndex = prefixLength % 8;
        
        if (byteIndex >= bytes.length) {
            return false;
        }
        
        // Проверяем конкретный бит
        return (bytes[byteIndex] & (1 << (7 - bitIndex))) == 0;
    }

    /**
     * @return responsibility first bits count of unid
     */
    public int getKResponsibility() {
        return kResponsibility;
    }
    
    /**
     * Получить максимальную емкость бакета
     * @return максимальное количество узлов
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    /**
     * Получить префикс бакета
     * @return префикс бакета
     */
    public KademliaId getPrefix() {
        return prefix;
    }
    
    /**
     * Получить длину префикса бакета
     * @return длина префикса в битах
     */
    public int getPrefixLength() {
        return prefixLength;
    }
    
    /**
     * Получить родительский бакет
     * @return родительский бакет или null, если это корневой бакет
     */
    public Bucket getParentBucket() {
        return parentBucket;
    }
    
    /**
     * Получить левый дочерний бакет
     * @return левый дочерний бакет или null, если бакет не разделен
     */
    public Bucket getLeftChild() {
        return leftChild;
    }
    
    /**
     * Получить правый дочерний бакет
     * @return правый дочерний бакет или null, если бакет не разделен
     */
    public Bucket getRightChild() {
        return rightChild;
    }
    
    /**
     * Останавливает планировщик задач при уничтожении бакета
     */
    public void shutdown() {
        scheduler.shutdown();
        
        // Останавливаем планировщики дочерних бакетов
        if (isSubdivided()) {
            leftChild.shutdown();
            rightChild.shutdown();
        }
    }
}

