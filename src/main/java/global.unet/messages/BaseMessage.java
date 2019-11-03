package global.unet.messages;

import global.unet.id.NodeInfoHolder;
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
        //TOdo заменить им preBuilder
        NodeInfoHolder nodeInfoHolder;

        UUID messageId;
        UnionId networkId;
        NodeInfo source;
        NodeInfo destination;
        int hopes = HOPES_DEFAULT;

        public BaseBuilder(NodeInfoHolder nodeInfoHolder, Class<? extends MessageType> clazz) {
            this.nodeInfoHolder = nodeInfoHolder;
            preBuilder = isRequest(clazz)
                    ? new CommonFieldBuilder(nodeInfoHolder)::fillMessageAsRequest
                    : new CommonFieldBuilder(nodeInfoHolder)::fillMessageAsResponse;
        }



        public static <T extends MessageType> boolean isRequest(Class<T> clazz){
            //TODO проверить что является типом MessageType
            Class<?>[] interfaces0 = clazz.getInterfaces();
            for (Class interface1 : interfaces0 ){
                if (interface1.equals(MessageType.Request.class))
                    return true;
            }
            return false;
        }

        BaseBuilder(Consumer<BaseBuilder<T>> preBuilder) {
            this.preBuilder = preBuilder;
        }

        //Важный момент BaseBuilder<T> , без него  в методе T build вовзаращается
        // не наследник fillMessageAsRequest, а fillMessageAsRequest потому что в сеттерах вернеться объект,
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

        public T build() {
            preBuilder.accept(this);
            return finalBuild();
        }

        abstract T finalBuild();


    }


    public interface MessageBuilderFabric<T extends Message, U extends BaseBuilder<T>> {
        U builder();
    }


    /**
     * создает сообщения , а также отвечает за генерацию ID сообщения
     * Для одного юниона работает
     */
    public static class CommonFieldBuilder {

        final UnionId networkId;
        final NodeInfo source;

        public CommonFieldBuilder(NodeInfoHolder nodeInfoHolder) {
            this.networkId = nodeInfoHolder.networkId;
            this.source = nodeInfoHolder.source;
        }

        /**
         * Заполняет
         * setSource(source)
         * networkId
         * UUID -> randomUUID
         * Подразумевается, что это сообщение инициатор. В котором будет сгенерировано idсобщения
         * //TODO id сообщения на самом деле id сессии общения же?
         *
         * @param <T>
         * @param pongBuilder
         * @return
         */
        public <T extends Message> BaseBuilder<T> fillMessageAsRequest(BaseBuilder<T> pongBuilder) {
            return fillBaseFieldsOfMsg(pongBuilder)
                    .setMessageId(generateUUID());
        }

        public <T extends Message> void fillMessageAsResponse(BaseBuilder<T> pongBuilder) {
            fillBaseFieldsOfMsg(pongBuilder);
        }

        /**
         * Заполняет
         * source
         * networkId
         *
         * @param pongBuilder
         * @param <T>
         * @return
         */
        private <T extends Message> BaseBuilder<T> fillBaseFieldsOfMsg(BaseBuilder<T> pongBuilder) {
            return pongBuilder
                    .setSource(source)
                    .setNetworkId(networkId);
        }

        private UUID generateUUID() {
            return UUID.randomUUID();
        }
    }
}
