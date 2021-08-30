package global.unet.application.receiver;

import global.unet.domain.messages.Message;
import global.unet.domain.protocol.MessageHandler;
import global.unet.domain.receiver.MessageBusDriver;

import java.util.HashMap;

public class SynchronousMessageBusDriverAdaptor implements MessageBusDriver {

    private final HashMap<Class<? extends Message>, MessageHandler<? extends Message>> handlerByMessage;

     public SynchronousMessageBusDriverAdaptor(HashMap<Class<? extends Message>, MessageHandler<? extends Message>> message2MessageHandler) {
        handlerByMessage = message2MessageHandler;
    }
    public SynchronousMessageBusDriverAdaptor() {
        this.handlerByMessage = new HashMap<>();
    }

    public <T extends Message> void addHandlerMapping(Class<T> messageClass, MessageHandler<T> messageHandler){
        handlerByMessage.put(messageClass,messageHandler);
    }

    private <T extends Message> MessageHandler<T> resolveMessageHandler(Class<? extends Message> message) {
        return (MessageHandler<T>) handlerByMessage.get(message);
    }

    @Override
    public <T extends Message> void handle(T message) {
        resolveMessageHandler(message.getClass()).handle(message);
    }
}
