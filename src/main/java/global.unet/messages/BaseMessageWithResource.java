package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.messages.builders.BaseMessageBuilder;
import global.unet.structures.NodeInfo;

import java.util.UUID;
import java.util.function.Consumer;

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

    public abstract static class MessageBuilder<T extends BaseMessageWithResource> extends BaseMessageBuilder<T> {

        UnionId resource;

        public MessageBuilder<T> setResource(UnionId resource) {
            this.resource = resource;
            return this;
        }
    }
}
