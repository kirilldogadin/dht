package global.unet.messages;

import global.unet.id.GlobalUnionId;
import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * Запрос на
 * Используется также для входа
 */
public class UnionBootstrap extends BaseMessageWithResource {

    private final GlobalUnionId targetUnion;

    public UnionBootstrap(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource, GlobalUnionId targetUnion) {
        super(source, destination, networkId, messageId, hopes, resource);
        this.targetUnion = targetUnion;
    }

    public UnionBootstrap(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, UnionId resource, GlobalUnionId targetUnion) {
        super(source, destination, networkId, messageId, resource);
        this.targetUnion = targetUnion;
    }


    public GlobalUnionId getTargetUnion() {
        return targetUnion;
    }

}
