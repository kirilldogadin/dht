package global.unet.domain.structures;

import global.unet.domain.id.KademliaId;
import global.unet.domain.id.UnionId;
import global.unet.domain.node.NodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса Bucket с LRU логикой
 */
public class BucketTest {

    private Bucket bucket;
    private static final int MAX_CAPACITY = 5;
    private static final int K_RESPONSIBILITY = 3;

    @BeforeEach
    public void setUp() {
        bucket = new Bucket(MAX_CAPACITY, K_RESPONSIBILITY);
    }

    /**
     * Создает NodeInfo с уникальным ID
     */
    private NodeInfo createNodeInfo(int seed) {
        byte[] bytes = new byte[20]; // KademliaId.BIT_COUNT / 8 = 160 / 8 = 20
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i + seed);
        }
        UnionId id = new KademliaId(bytes);
        try {
            URI uri = new URI("udp://node" + seed + ".example.com:4000");
            return new NodeInfo(uri, id, 4000 + seed);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddNode() {
        NodeInfo node = createNodeInfo(1);
        assertTrue(bucket.add(node));
        assertEquals(1, bucket.size());
        assertTrue(bucket.contains(node));
    }

    @Test
    public void testAddDuplicateNode() {
        NodeInfo node = createNodeInfo(1);
        assertTrue(bucket.add(node));
        assertFalse(bucket.add(node)); // Второе добавление должно вернуть false
        assertEquals(1, bucket.size()); // Размер не должен измениться
    }

    @Test
    public void testLRULogic() {
        // Добавляем MAX_CAPACITY узлов
        for (int i = 0; i < MAX_CAPACITY; i++) {
            bucket.add(createNodeInfo(i));
        }
        
        assertEquals(MAX_CAPACITY, bucket.size());
        
        // Добавляем еще один узел, что должно вызвать удаление наименее недавно использованного
        NodeInfo newNode = createNodeInfo(MAX_CAPACITY);
        assertTrue(bucket.add(newNode));
        
        assertEquals(MAX_CAPACITY, bucket.size()); // Размер не должен превышать MAX_CAPACITY
        assertTrue(bucket.contains(newNode)); // Новый узел должен быть добавлен
        
        // Проверяем, что первый добавленный узел (наименее недавно использованный) был удален
        assertFalse(bucket.contains(createNodeInfo(0)));
    }

    @Test
    public void testUpdateNode() {
        // Добавляем MAX_CAPACITY узлов
        for (int i = 0; i < MAX_CAPACITY; i++) {
            bucket.add(createNodeInfo(i));
        }
        
        // Обновляем первый узел
        NodeInfo firstNode = createNodeInfo(0);
        assertTrue(bucket.update(firstNode));
        
        // Добавляем новый узел, что должно вызвать удаление наименее недавно использованного
        NodeInfo newNode = createNodeInfo(MAX_CAPACITY);
        assertTrue(bucket.add(newNode));
        
        // Проверяем, что первый узел не был удален, так как он был обновлен
        assertTrue(bucket.contains(firstNode));
        
        // Проверяем, что второй узел (теперь наименее недавно использованный) был удален
        assertFalse(bucket.contains(createNodeInfo(1)));
    }

    @Test
    public void testGetNodeInfoList() {
        // Добавляем несколько узлов
        for (int i = 0; i < 3; i++) {
            bucket.add(createNodeInfo(i));
        }
        
        List<NodeInfo> nodes = bucket.getNodeInfoList();
        assertEquals(3, nodes.size());
        
        // Проверяем, что изменение возвращенного списка не влияет на бакет
        nodes.clear();
        assertEquals(3, bucket.size());
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        final int numThreads = 10;
        final int nodesPerThread = 10;
        final CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < nodesPerThread; i++) {
                        NodeInfo node = createNodeInfo(threadId * nodesPerThread + i);
                        bucket.add(node);
                        
                        // Иногда обновляем узел
                        if (i % 3 == 0) {
                            bucket.update(node);
                        }
                        
                        // Иногда проверяем наличие узла
                        if (i % 2 == 0) {
                            bucket.contains(node);
                        }
                        
                        // Иногда получаем список узлов
                        if (i % 5 == 0) {
                            bucket.getNodeInfoList();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Проверяем, что размер бакета не превышает максимальную емкость
        assertTrue(bucket.size() <= MAX_CAPACITY);
    }

    @Test
    public void testNodeActivityTracking() throws Exception {
        NodeInfo node = createNodeInfo(1);
        bucket.add(node);
        
        // Проверяем, что узел активен по умолчанию
        assertTrue(node.isActive());
        assertEquals(0, node.getFailedAttempts());
        
        // Симулируем неудачную попытку связи
        assertEquals(1, node.incrementFailedAttempts());
        assertEquals(2, node.incrementFailedAttempts());
        
        // Сбрасываем счетчик неудачных попыток
        node.resetFailedAttempts();
        assertEquals(0, node.getFailedAttempts());
        
        // Устанавливаем узел как неактивный
        node.setActive(false);
        assertFalse(node.isActive());
        
        // Обновляем время последней активности
        Instant before = node.getLastSeen();
        Thread.sleep(10); // Небольшая задержка
        node.updateLastSeen();
        
        // Проверяем, что время обновилось и узел снова активен
        assertTrue(node.getLastSeen().isAfter(before));
        assertTrue(node.isActive());
        assertEquals(0, node.getFailedAttempts());
    }

    @Test
    public void testPingCallback() {
        AtomicInteger pingCount = new AtomicInteger(0);
        
        // Устанавливаем колбэк для пинга
        bucket.setPingCallback(node -> {
            pingCount.incrementAndGet();
            // Симулируем успешный пинг
            node.updateLastSeen();
        });
        
        // Добавляем MAX_CAPACITY узлов
        for (int i = 0; i < MAX_CAPACITY; i++) {
            bucket.add(createNodeInfo(i));
        }
        
        // Добавляем еще один узел, что должно вызвать пинг наименее недавно использованного
        bucket.add(createNodeInfo(MAX_CAPACITY));
        
        // Проверяем, что был выполнен пинг
        assertEquals(1, pingCount.get());
    }

    @Test
    public void testGetKResponsibility() {
        assertEquals(K_RESPONSIBILITY, bucket.getKResponsibility());
    }

    @Test
    public void testGetMaxCapacity() {
        assertEquals(MAX_CAPACITY, bucket.getMaxCapacity());
    }
    
    @Test
    public void testBucketSubdivision() throws Exception {
        // Создаем бакет с префиксом
        byte[] prefixBytes = new byte[20];
        KademliaId prefix = new KademliaId(prefixBytes);
        Bucket rootBucket = new Bucket(2, K_RESPONSIBILITY, prefix, 0);
        
        // Добавляем узлы с разными битами на первой позиции
        byte[] bytes1 = new byte[20];
        bytes1[0] = (byte) 0x80; // 1000 0000
        KademliaId id1 = new KademliaId(bytes1);
        NodeInfo node1 = new NodeInfo(new URI("udp://node1.example.com:4001"), id1, 4001);
        
        byte[] bytes2 = new byte[20];
        bytes2[0] = (byte) 0x00; // 0000 0000
        KademliaId id2 = new KademliaId(bytes2);
        NodeInfo node2 = new NodeInfo(new URI("udp://node2.example.com:4002"), id2, 4002);
        
        // Добавляем узлы в бакет
        rootBucket.add(node1);
        rootBucket.add(node2);
        
        // Добавляем третий узел, что должно вызвать разделение бакета
        byte[] bytes3 = new byte[20];
        bytes3[0] = (byte) 0x40; // 0100 0000
        KademliaId id3 = new KademliaId(bytes3);
        NodeInfo node3 = new NodeInfo(new URI("udp://node3.example.com:4003"), id3, 4003);
        
        rootBucket.add(node3);
        
        // Проверяем, что бакет был разделен
        assertTrue(rootBucket.isSubdivided());
        
        // Проверяем, что узлы распределены по дочерним бакетам
        Bucket leftChild = rootBucket.getLeftChild();
        Bucket rightChild = rootBucket.getRightChild();
        
        assertNotNull(leftChild);
        assertNotNull(rightChild);
        
        // node2 и node3 должны быть в левом бакете (первый бит 0)
        assertTrue(leftChild.contains(node2));
        assertTrue(leftChild.contains(node3));
        
        // node1 должен быть в правом бакете (первый бит 1)
        assertTrue(rightChild.contains(node1));
        
        // Проверяем, что родительский бакет правильно установлен
        assertEquals(rootBucket, leftChild.getParentBucket());
        assertEquals(rootBucket, rightChild.getParentBucket());
        
        // Проверяем длину префикса
        assertEquals(0, rootBucket.getPrefixLength());
        assertEquals(1, leftChild.getPrefixLength());
        assertEquals(1, rightChild.getPrefixLength());
    }
    
    @Test
    public void testShutdown() {
        // Проверяем, что метод shutdown не вызывает исключений
        bucket.shutdown();
    }
}

