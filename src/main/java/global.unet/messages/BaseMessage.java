package global.unet.messages;

import global.unet.id.UnionNodeInfo;
import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Базовую информацию
 */
public abstract class BaseMessage implements Message {

    public static final int HOPES_DEFAULT = 50;

    final UUID messageId;
    final UnionId networkId;
    final NodeInfo source;
    final NodeInfo destination;
    final int hopes;

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


}


