package global.unet.server;

import global.unet.exception.UnionServerException;
import global.unet.messages.Message;

import java.util.function.Consumer;

public abstract class BaseServer implements Server {

    //TODo можно переписать на фабрику которая будет
    Consumer<Message> messageHandler;
    private boolean messageHandlerIsSet = false;

    protected BaseServer() {

    }


    //Todo блокировку на messageHandlerIsSet
    @Override
    public void setMessageHandler(Consumer<Message> messageHandler) {
        this.messageHandler = messageHandler;
        messageHandlerIsSet = true;

    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void start() {
        checkMessageHandler();

    }

    private void checkMessageHandler(){
        if (!messageHandlerIsSet){
            throw new UnionServerException("Didn't set a messageHandler");
        }
    }
}
