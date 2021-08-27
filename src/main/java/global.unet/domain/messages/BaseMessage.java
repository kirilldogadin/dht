package global.unet.domain.messages;

import global.unet.domain.id.UnionId;
import global.unet.domain.structures.NodeInfo;

import java.util.Objects;
import java.util.UUID;

/**
 * Базовую информацию
 */
public abstract class BaseMessage implements Message {

    //TODO константа?
    public static final int HOPES_DEFAULT = 50;

    protected final UUID messageId;
    protected final UnionId networkId;
    protected final NodeInfo source;
    protected final NodeInfo destination;
    protected final int hopes;

    protected BaseMessage(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        this.messageId = messageId;
        this.networkId = networkId;
        this.source = source;
        this.destination = destination;
        this.hopes = hopes;
    }

    protected BaseMessage(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseMessage)) return false;
        BaseMessage that = (BaseMessage) o;
        return hopes == that.hopes && messageId.equals(that.messageId) && networkId.equals(that.networkId) && source.equals(that.source) && destination.equals(that.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, networkId, source, destination, hopes);
    }
}


