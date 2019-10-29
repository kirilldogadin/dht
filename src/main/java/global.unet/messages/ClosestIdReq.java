package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

/**
 * Запрос на поиск ближайшнего к Id ноды
 */
public class ClosestIdReq extends InitReq {

    private final UnionId resource;

    public ClosestIdReq(NodeInfo source, UnionId resource, int hopes) {
        super(source, hopes);
        this.resource = resource;
    }

    public ClosestIdReq(NodeInfo source, UnionId resource) {
        super(source);
        this.resource = resource;
    }

    public UnionId getResource() {
        return resource;
    }

}
