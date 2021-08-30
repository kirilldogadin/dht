package global.unet.domain.messages;

import global.unet.domain.id.UnionId;
import global.unet.domain.structures.NodeInfo;

import java.util.UUID;

/**
 * Базовые поля с источником
 */
public abstract class BaseMessageWithResource extends BaseMessage{

    private final UnionId resource;

    public BaseMessageWithResource(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource) {
        super(source, destination, networkId, messageId, hopes);
        this.resource = resource;
    }

    public BaseMessageWithResource(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, UnionId resource) {
        super(source, destination, networkId, messageId);
        this.resource = resource;
    }

    public UnionId getResource() {
        return resource;
    }
}
