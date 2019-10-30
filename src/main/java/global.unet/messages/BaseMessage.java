package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * Базовую информацию
 */
public abstract class BaseMessage implements Message {

    public static final int HOPES_DEFAULT = 50;

    private final UUID messageId;
    private final UnionId networkId;
    private final NodeInfo source;
    private final NodeInfo destination;
    private final int hopes;

    public BaseMessage(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId,  int hopes) {
        this.messageId = messageId;
        this.networkId = networkId;
        this.source = source;
        this.destination = destination;
        this.hopes = hopes;
    }

    public BaseMessage(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId) {
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
}
