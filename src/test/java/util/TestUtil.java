package util;

import global.unet.id.KademliaId;
import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;
import global.unet.structures.XorTreeRoutingTable;

import java.net.URI;
import java.net.URISyntaxException;

//todo из тестов в Ютилс основного пакета
public class TestUtil {

    public static void main(String[] args) {
        KademliaId generatedID = generateUnid();
        System.out.println(generatedID.asBytes());
    }

    public static KademliaId generateUnid() {
        return generateUnid(KademliaId.BIT_COUNT);
    }

    /**
     * используется для генерации тестов, но не криптостойко
     *
     * @param bitsCount
     * @return
     */
    public static byte[] generateUnidAsByteArray(int bitsCount) {
        byte[] id = new byte[bitsCount / Byte.SIZE];
        for (int i = 0; i < id.length; i++) {
            id[i] = (byte) ((int) (Math.random() * 256));
        }
        return id;
    }

    public static KademliaId generateUnid(int bitsCount) {
        return new KademliaId(generateUnidAsByteArray(bitsCount));
    }

    /**
     * Устанавливает нужный байт, остальные заполняет дефолтным
     * Big endian
     * Номер Байты и биты слева направа. второй значит 0100 0000
     *
     * @param byteNumber   номер байта, который надо установить
     * @param settableByte значение в которое надо установить , например (byte) 128
     * @param defaultValue в какое из значений установить остальные биты 0/1
     * @return unionId
     */
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


    public static UnionId createKademliaFixedId() {
        //0000 0000 0111 1111
        KademliaId selfId = createKademliaIdByTemplate(1, (byte) 127, (byte) 0);
        return selfId;

    }

    public static UnionId createKademliaFixedId2() {
        //0000 0000 0010 0000
        return createKademliaIdByTemplate(1, (byte) 32, (byte) 0);
    }

    public static XorTreeRoutingTable createRoutingTable() {
        UnionId kademliaFixedId = createKademliaFixedId();
        XorTreeRoutingTable routingTable = new XorTreeRoutingTable(kademliaFixedId);
        return routingTable;
    }

    public static NodeInfo nodeInfo1() {
        try {
            return new NodeInfo(new URI("0.0.0.0"), createKademliaFixedId2(), 228);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}
