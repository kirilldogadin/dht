package global.unet.id;

import global.unet.routing.table.XorTreeRoutingTable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XorTreeRoutingTableTest {

    @Test
    public void findBucketNumberTest() {

        //0000 0000 0111 1111
        KademliaId selfId = createKademliaIdByTemplate(1,(byte) 127, (byte) 0);

        //0000 0000 0010 0000
        KademliaId distanse = createKademliaIdByTemplate(1,(byte) 32, (byte) 0);

        XorTreeRoutingTable routingTable = new XorTreeRoutingTable(selfId, null);

        int bucketNumberForUnid = routingTable.getBucketNumberForUnid(distanse.asBytes());
        assertEquals(10,bucketNumberForUnid);
    }


    @Test
    public void findBucketTest() {

        //0000 0000 0010 0000
        KademliaId selfId = createKademliaIdByTemplate(1,(byte) 32, (byte) 0);
        //0000 0000 0111 1111
        KademliaId toId = createKademliaIdByTemplate(1,(byte) 127, (byte) 0);

        XorTreeRoutingTable routingTable = new XorTreeRoutingTable(selfId, null);
        routingTable.findBucket(toId);


    }

    /**
     * Устанавливает нужный байт, остальные заполняет дефолтным
     * Big endian
     * Номер Байты и биты слева направа. второй значит 0100 0000
     * @param byteNumber   номер байта, который надо установить
     * @param settableByte значение в которое надо установить , например (byte) 128
     * @param defaultValue в какое из значений установить остальные биты 0/1
     * @return unionId
     */
    //Todo ПЕРЕНЕСТИ в Utils
    public static KademliaId createKademliaIdByTemplate(int byteNumber, byte settableByte, byte defaultValue) {
        int byteCount = KademliaId.BIT_COUNT / Byte.SIZE;
        byte[] bytes = new byte[byteCount];

        if (byteNumber > byteCount - 1 || byteNumber < 0) {
            throw new IllegalArgumentException("byte number must be < " + byteCount + "and > 0");
        }
        for (byte curByte : bytes) {
            curByte = defaultValue;
        }
        bytes[byteNumber] = settableByte;
        return new KademliaId(bytes);
    }
}
