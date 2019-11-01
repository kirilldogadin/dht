package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * Базовую информацию
 */
public abstract class BaseMessage<T extends BaseMessage> implements Message<T> {

    static final int HOPES_DEFAULT = 50;

    private final UUID messageId;
    private final UnionId networkId;
    private final NodeInfo source;
    private final NodeInfo destination;
    private final int hopes;

    BaseMessage(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        this.messageId = messageId;
        this.networkId = networkId;
        this.source = source;
        this.destination = destination;
        this.hopes = hopes;
    }

    BaseMessage(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId) {
        this(source, destination, networkId, messageId, HOPES_DEFAULT);
    }

    public NodeInfo getSource() {
        return source;
    }

    public int getHopes() {
        return hopes;
    }

    public UnionId getNetworkId() {
        return networkId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public NodeInfo getDestination() {
        return destination;
    }

    public static abstract class BaseBuilder<T> implements Builder{
        UUID messageId;
        UnionId networkId;
        NodeInfo source;
        NodeInfo destination;
        int hopes = HOPES_DEFAULT;

        BaseBuilder setMessageId(UUID messageId) {
            this.messageId = messageId;
            return this;
        }

        BaseBuilder setNetworkId(UnionId networkId) {
            this.networkId = networkId;
            return this;
        }

        BaseBuilder setSource(NodeInfo source) {
            this.source = source;
            return this;
        }

        public BaseBuilder setDestination(NodeInfo destination) {
            this.destination = destination;
            return this;
        }

        public BaseBuilder setHopes(int hopes) {
            this.hopes = hopes;
            return this;
        }

        public abstract T build();

    }


}
