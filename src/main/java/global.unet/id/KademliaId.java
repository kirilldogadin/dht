package global.unet.id;

public class KademliaId implements UnionId {

    public static final int BIT_COUNT = 160;
    private final byte[] bytesId;

    String idExceptionPrefix = "not valid Id.";

    public KademliaId(byte[] bytes) {
        checkValid(bytes);
        bytesId = bytes;
    }


    private void checkValid(byte[] bytes) {
        if (bytes.length != BIT_COUNT / Byte.SIZE)
            throw new IllegalArgumentException(idExceptionPrefix
                    + "Length must be "
                    + BIT_COUNT);
    }

    @Override
    public int getSpaceOfUnionId() {
        return BIT_COUNT;
    }

    @Override
    public UnionId computeDistanceAsUnionId(UnionId in) {
        return null;
    }

    @Override
    public byte[] computeDistance(byte[] from) {
       return computeDistance(from, this.asBytes());
    }

    @Override
    public byte[] computeDistance(byte[] from, byte[] to) {

        byte[] distance = new byte[from.length];
        for (int i = 0; i < from.length; i++) {
            distance[i] = (byte) (from[i] ^ to[i]);
        }
        return distance;
    }

    @Override
    public byte[] asBytes() {
        return bytesId;
    }
}
