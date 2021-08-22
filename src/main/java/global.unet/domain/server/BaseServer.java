package global.unet.domain.server;

import global.unet.domain.exception.UnionServerException;
import global.unet.domain.messages.Message;

import java.util.function.Consumer;

public abstract class BaseServer implements Server {

    //TODo можно переписать на фабрику которая будет
    Consumer<Message> messageHandler;
    private boolean messageHandlerIsSet = false;
    boolean isRunning = false;

    protected BaseServer() {

    }


    //Todo блокировку на messageHandlerIsSet
    @Override
    public void setMessageHandler(Consumer<Message> messageHandler) {
        this.messageHandler = messageHandler;
        messageHandlerIsSet = true;

    }



    protected void checkMessageHandler(){
        if (!messageHandlerIsSet){
            throw new UnionServerException("Didn't set a messageHandler");
        }
    }
}
