package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Базовую информацию
 */
public abstract class BaseMessage<T extends BaseMessage> implements Message<T> {

    static final int HOPES_DEFAULT = 50;

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

    public static abstract class BaseBuilder<T extends Message> {
        final Consumer<BaseBuilder<T>> preBuilder;

        UUID messageId;
        UnionId networkId;
        NodeInfo source;
        NodeInfo destination;
        int hopes = HOPES_DEFAULT;

        BaseBuilder(Consumer<BaseBuilder<T>> preBuilder) {
            this.preBuilder = preBuilder;
        }

        //Важный момент BaseBuilder<T> , без него  в методе T build вовзаращается
        // не наследник createFullMessageRequest, а createFullMessageRequest потому что в сеттерах вернеться объект,
        // который может вернуть только BaseBuilder
        public BaseBuilder<T> setMessageId(UUID messageId) {
            this.messageId = messageId;
            return this;
        }

        BaseBuilder<T> setNetworkId(UnionId networkId) {
            this.networkId = networkId;
            return this;
        }

        BaseBuilder<T> setSource(NodeInfo source) {
            this.source = source;
            return this;
        }

        public BaseBuilder<T> setDestination(NodeInfo destination) {
            this.destination = destination;
            return this;
        }

        public BaseBuilder<T> setHopes(int hopes) {
            this.hopes = hopes;
            return this;
        }

        public T build(){
            preBuilder.accept(this);
            return finalBuild();
        }

        abstract T finalBuild();

    }


}
