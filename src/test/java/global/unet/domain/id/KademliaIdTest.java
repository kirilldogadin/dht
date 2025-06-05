package global.unet.domain.id;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса KademliaId
 */
public class KademliaIdTest {

    @Test
    public void testCreateKademliaId() {
        // Создаем массив байтов правильной длины (160 бит = 20 байт)
        byte[] bytes = new byte[20];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }
        
        // Проверяем, что конструктор не выбрасывает исключение
        KademliaId id = new KademliaId(bytes);
        assertNotNull(id);
        
        // Проверяем, что метод asBytes возвращает тот же массив
        assertArrayEquals(bytes, id.asBytes());
    }
    
    @Test
    public void testCreateKademliaIdWithInvalidLength() {
        // Создаем массив байтов неправильной длины
        byte[] bytes = new byte[10]; // Должно быть 20 байт
        
        // Проверяем, что конструктор выбрасывает исключение
        assertThrows(IllegalArgumentException.class, () -> new KademliaId(bytes));
    }
    
    @Test
    public void testCreateKademliaIdFromString() {
        // Создаем строку
        String str = "TestKademliaId12345";
        
        // Проверяем, что конструктор выбрасывает исключение, так как длина не соответствует
        assertThrows(IllegalArgumentException.class, () -> new KademliaId(str));
    }
    
    @Test
    public void testComputeDistance() {
        // Создаем два ID
        byte[] bytes1 = new byte[20];
        byte[] bytes2 = new byte[20];
        
        // Заполняем первый ID
        for (int i = 0; i < bytes1.length; i++) {
            bytes1[i] = (byte) i;
        }
        
        // Заполняем второй ID
        for (int i = 0; i < bytes2.length; i++) {
            bytes2[i] = (byte) (i + 10);
        }
        
        KademliaId id1 = new KademliaId(bytes1);
        KademliaId id2 = new KademliaId(bytes2);
        
        // Вычисляем расстояние
        byte[] distance = id1.computeDistance(id2);
        
        // Проверяем результат XOR
        for (int i = 0; i < distance.length; i++) {
            assertEquals((byte) (i ^ (i + 10)), distance[i]);
        }
    }
    
    @Test
    public void testComputeDistanceAsUnionId() {
        // Создаем два ID
        byte[] bytes1 = new byte[20];
        byte[] bytes2 = new byte[20];
        
        // Заполняем первый ID
        for (int i = 0; i < bytes1.length; i++) {
            bytes1[i] = (byte) i;
        }
        
        // Заполняем второй ID
        for (int i = 0; i < bytes2.length; i++) {
            bytes2[i] = (byte) (i + 10);
        }
        
        KademliaId id1 = new KademliaId(bytes1);
        KademliaId id2 = new KademliaId(bytes2);
        
        // Вычисляем расстояние как UnionId
        UnionId distanceId = id1.computeDistanceAsUnionId(id2);
        
        // Проверяем, что результат - это KademliaId
        assertTrue(distanceId instanceof KademliaId);
        
        // Проверяем результат XOR
        byte[] distance = distanceId.asBytes();
        for (int i = 0; i < distance.length; i++) {
            assertEquals((byte) (i ^ (i + 10)), distance[i]);
        }
    }
    
    @Test
    public void testEquals() {
        // Создаем два одинаковых ID
        byte[] bytes = new byte[20];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }
        
        KademliaId id1 = new KademliaId(bytes);
        KademliaId id2 = new KademliaId(bytes.clone());
        
        // Проверяем, что они равны
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        
        // Создаем другой ID
        byte[] bytes2 = new byte[20];
        for (int i = 0; i < bytes2.length; i++) {
            bytes2[i] = (byte) (i + 1);
        }
        
        KademliaId id3 = new KademliaId(bytes2);
        
        // Проверяем, что они не равны
        assertNotEquals(id1, id3);
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
    
    @Test
    public void testCompareTo() {
        // Создаем три ID для сравнения
        byte[] bytes1 = new byte[20];
        byte[] bytes2 = new byte[20];
        byte[] bytes3 = new byte[20];
        
        // ID1 < ID2 < ID3
        bytes1[0] = 1;
        bytes2[0] = 2;
        bytes3[0] = 3;
        
        KademliaId id1 = new KademliaId(bytes1);
        KademliaId id2 = new KademliaId(bytes2);
        KademliaId id3 = new KademliaId(bytes3);
        
        // Проверяем сравнение
        assertTrue(id1.compareTo(id2) < 0);
        assertTrue(id2.compareTo(id3) < 0);
        assertTrue(id3.compareTo(id1) > 0);
        
        // Проверяем равенство
        assertEquals(0, id1.compareTo(new KademliaId(bytes1.clone())));
    }
    
    @Test
    public void testGetCommonPrefixLength() {
        // Создаем ID с разной длиной общего префикса
        
        // ID1 и ID2 имеют общий префикс 12 бит (1.5 байта)
        byte[] bytes1 = new byte[20];
        byte[] bytes2 = new byte[20];
        
        // Первый байт одинаковый
        bytes1[0] = (byte) 0xF0; // 1111 0000
        bytes2[0] = (byte) 0xF0; // 1111 0000
        
        // Второй байт отличается после 4-го бита
        bytes1[1] = (byte) 0xA0; // 1010 0000
        bytes2[1] = (byte) 0xAF; // 1010 1111
        
        KademliaId id1 = new KademliaId(bytes1);
        KademliaId id2 = new KademliaId(bytes2);
        
        // Проверяем длину общего префикса
        assertEquals(12, id1.getCommonPrefixLength(id2));
        
        // ID3 и ID4 имеют общий префикс 8 бит (1 байт)
        byte[] bytes3 = new byte[20];
        byte[] bytes4 = new byte[20];
        
        bytes3[0] = (byte) 0xFF; // 1111 1111
        bytes4[0] = (byte) 0xFF; // 1111 1111
        bytes3[1] = (byte) 0x00; // 0000 0000
        bytes4[1] = (byte) 0x80; // 1000 0000
        
        KademliaId id3 = new KademliaId(bytes3);
        KademliaId id4 = new KademliaId(bytes4);
        
        assertEquals(8, id3.getCommonPrefixLength(id4));
        
        // ID5 и ID6 полностью совпадают
        byte[] bytes5 = new byte[20];
        byte[] bytes6 = bytes5.clone();
        
        KademliaId id5 = new KademliaId(bytes5);
        KademliaId id6 = new KademliaId(bytes6);
        
        assertEquals(160, id5.getCommonPrefixLength(id6));
    }
}

