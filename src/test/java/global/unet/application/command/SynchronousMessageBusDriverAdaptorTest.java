package global.unet.application.command;

import global.unet.domain.messages.Message;
import global.unet.domain.protocol.MessageHandler;
import global.unet.domain.protocol.ping.PingMessageRequest;
import org.junit.Test;

import java.util.HashMap;

public class SynchronousMessageBusDriverAdaptorTest {

    @Test
    public void testHandle() {

        HashMap<Class<? extends Message>, MessageHandler<? extends Message>> message2MessageHandler = new HashMap<>();
        //TODO тест на PingMessage
        message2MessageHandler.put(PingMessageRequest.class,
                new MessageHandler<Message>() {
                    @Override
                    public void handle(Message message) {
                        System.out.println(SynchronousMessageBusDriverAdaptor.class.getName() +
                                "work, testing message:"
                                + message);
                    }
                });
        SynchronousMessageBusDriverAdaptor synchronousMessageBusDriverAdaptor
                = new SynchronousMessageBusDriverAdaptor(message2MessageHandler);
        PingMessageRequest testingMessage = new PingMessageRequest(null, null, null, null, 0);
        synchronousMessageBusDriverAdaptor.handle(testingMessage);
    }
}