package util;

import global.unet.id.KademliaId;

import java.util.UUID;

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

    //TODO Пока не работает т.к. в камделиа зашит 160бит
    public static KademliaId generate128bitUnid() {
        //TOdo передалть на 128 бит
        return new KademliaId(UUID
                .randomUUID()
                .toString()
                .getBytes());
    }
}
