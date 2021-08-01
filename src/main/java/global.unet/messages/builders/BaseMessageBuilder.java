package global.unet.messages.builders;

import global.unet.id.UnionId;
import global.unet.id.UnionNodeInfo;
import global.unet.messages.BaseMessage;
import global.unet.messages.Message;
import global.unet.messages.MessageType;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * Билдер - это контейнер, который превратится в финальный класс
 *
 * @param <T>
 */
public abstract class BaseMessageBuilder<T extends Message> implements MessageBuilder<T> {
    protected UUID messageId;
    protected UnionId networkId;
    protected NodeInfo source;
    protected NodeInfo destination;
    protected int hopes = BaseMessage.HOPES_DEFAULT;

    public BaseMessageBuilder() {
    }


    private UUID generateUUID() {
        return UUID.randomUUID();
    }

    public static <T extends MessageType> boolean isRequest(Class<?> clazz) {
        //TODO проверить что является типом MessageType
        Class<?>[] interfaces0 = clazz.getInterfaces();
        for (Class interface1 : interfaces0) {
            if (interface1.equals(MessageType.Request.class))
                return true;
        }
        return false;
    }

    //Важный момент BaseBuilder<T> , без него  в методе T build вовзаращается
    // не наследник fillMessageAsRequest, а fillMessageAsRequest потому что в сеттерах вернеться объект,
    // который может вернуть только BaseBuilder
    public BaseMessageBuilder<T> setMessageId(UUID messageId) {
        this.messageId = messageId;
        return this;
    }

    BaseMessageBuilder<T> setNetworkId(UnionId networkId) {
        this.networkId = networkId;
        return this;
    }

    BaseMessageBuilder<T> setSource(NodeInfo source) {
        this.source = source;
        return this;
    }

    public BaseMessageBuilder<T> setDestination(NodeInfo destination) {
        this.destination = destination;
        return this;
    }

    public BaseMessageBuilder<T> setHopes(int hopes) {
        this.hopes = hopes;
        return this;
    }

    public abstract T build();


}
