package global.unet.domain.id;

/**
 * Реализация идентификатора для алгоритма Kademlia
 * Включает методы для вычисления XOR метрики, сравнения идентификаторов
 * и определения общего префикса
 */
public class KademliaId implements UnionId {

    public static final int BIT_COUNT = 160;
    private final byte[] bytesId;

    String idExceptionPrefix = "not valid Id.";

    public KademliaId(byte[] bytes) {
        checkValid(bytes);
        bytesId = bytes;
    }

    public KademliaId(String id) {
        this(id.getBytes());
    }

    private void checkValid(byte[] bytes) {
        if (bytes.length != BIT_COUNT / Byte.SIZE)
            throw new IllegalArgumentException(idExceptionPrefix
                    + "Length must be "
                    + BIT_COUNT);
    }

    @Override
    public int getUnidBitCount() {
        return BIT_COUNT;
    }

    @Override
    public UnionId computeDistanceAsUnionId(UnionId in) {
        // Реализация метода computeDistanceAsUnionId
        // Вычисляем XOR между текущим ID и входящим ID
        // и возвращаем результат как новый KademliaId
        byte[] distance = computeDistance(in);
        return new KademliaId(distance);
    }

    @Override
    public byte[] computeDistance(byte[] from) {
        return computeDistance(from, this.asBytes());
    }

    @Override
    public byte[] computeDistance(UnionId from) {
        return this.computeDistance(from.asBytes());
    }

    @Override
    public byte[] computeDistance(byte[] from, byte[] to) {
        byte[] distance = new byte[from.length];
        for (int i = 0; i < from.length; i++) {
            distance[i] = (byte) (from[i] ^ to[i]);
        }
        return distance;
    }

    /**
     * Сравнивает два идентификатора и возвращает результат сравнения
     * 
     * @param other идентификатор для сравнения
     * @return отрицательное число, если текущий ID меньше other,
     *         положительное число, если текущий ID больше other,
     *         0, если идентификаторы равны
     */
    public int compareTo(KademliaId other) {
        byte[] thisBytes = this.asBytes();
        byte[] otherBytes = other.asBytes();
        
        for (int i = 0; i < thisBytes.length; i++) {
            int thisByte = thisBytes[i] & 0xFF; // Беззнаковое значение байта
            int otherByte = otherBytes[i] & 0xFF;
            
            if (thisByte != otherByte) {
                return thisByte - otherByte;
            }
        }
        
        return 0; // Идентификаторы равны
    }
    
    /**
     * Определяет длину общего префикса между двумя идентификаторами в битах
     * 
     * @param other идентификатор для сравнения
     * @return количество совпадающих бит с начала идентификаторов
     */
    public int getCommonPrefixLength(KademliaId other) {
        byte[] thisBytes = this.asBytes();
        byte[] otherBytes = other.asBytes();
        
        int prefixLength = 0;
        
        // Сравниваем байты
        for (int i = 0; i < thisBytes.length; i++) {
            byte xor = (byte) (thisBytes[i] ^ otherBytes[i]);
            
            if (xor == 0) {
                // Если XOR равен 0, все биты в этом байте совпадают
                prefixLength += 8;
            } else {
                // Если есть различия, считаем количество совпадающих бит в этом байте
                int leadingZeros = Integer.numberOfLeadingZeros(xor & 0xFF) - 24;
                prefixLength += leadingZeros;
                break;
            }
        }
        
        return prefixLength;
    }

    @Override
    public byte[] asBytes() {
        return bytesId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        KademliaId that = (KademliaId) o;
        
        if (bytesId.length != that.bytesId.length) return false;
        
        for (int i = 0; i < bytesId.length; i++) {
            if (bytesId[i] != that.bytesId[i]) return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        for (byte b : bytesId) {
            result = 31 * result + b;
        }
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytesId) {
            sb.append(String.format("%02X", b));
        }
        return "KademliaId[" + sb.toString() + "]";
    }
}

