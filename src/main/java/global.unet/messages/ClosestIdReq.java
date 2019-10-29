package global.unet.messages;

import global.unet.id.UnionId;

/**
 * Запрос на поиск ближайшнего к Id ноды
 */
public class ClosestIdReq implements Message {

    private final UnionId unionId;
    private final int hopes;
    private static final int HOPES_DEFAULT = 50;

    public ClosestIdReq(UnionId unionId, int hopes) {
        this.unionId = unionId;
        this.hopes = hopes;
    }

    public ClosestIdReq(UnionId unionId) {
        this(unionId, HOPES_DEFAULT);
    }

    public UnionId getUnionId() {
        return unionId;
    }
}
