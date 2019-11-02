package global.unet.messages;

import global.unet.id.UnionId;
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

    public abstract static class Builder<T extends BaseMessageWithResource> extends BaseMessage.BaseBuilder<T>{

        UnionId resource;

        Builder(Consumer<BaseBuilder<T>> preBuilder) {
            super(preBuilder);
        }


        public Builder<T> setResource(UnionId resource) {
            this.resource = resource;
            return this;
        }
    }
}
