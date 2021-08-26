package global.unet.application.command;

import global.unet.domain.messages.Message;
import global.unet.domain.protocol.MessageHandler;
import global.unet.domain.receiver.MessageBusDriver;

import java.util.HashMap;

//TODO хреново, что требуются дженерики наружу
public class SynchronousMessageBusDriverAdaptor implements MessageBusDriver {

     private final HashMap handlerByMessage;

    public SynchronousMessageBusDriverAdaptor(HashMap<Class<? extends Message>, MessageHandler<? extends Message>> message2MessageHandler) {
        handlerByMessage = message2MessageHandler;
    }

    private <T extends Message> MessageHandler<T> resolveMessageHandler(Class<? extends Message> message) {
        return (MessageHandler<T>) handlerByMessage.get(message);
    }

    @Override
    public <T extends Message> void handle(T message) {
        resolveMessageHandler(message.getClass()).handle(message);

    }
}
