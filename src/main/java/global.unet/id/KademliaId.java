package global.unet.id;

public class KademliaId implements UnionId {

    public static final int SPACE_ID = 160;
    private final byte[] bytesId;



    public KademliaId(byte[] bytes) {
        if (!idIsValid(bytes)){
            throw new IllegalArgumentException("not valid Id");
        }
        bytesId = bytes;
    }

    private boolean idIsValid(byte[] bytes) {
        //todo check format
        return true;
    }

    @Override
    public int getSpaceOfUnionId() {
        return SPACE_ID;
    }

    @Override
    public UnionId computeDistanceAsUnionId(UnionId in) {
        return null;
    }

    @Override
    public byte[] computeDistance(UnionId in) {
        return new byte[0];
    }
}
