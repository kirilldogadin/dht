package global.unet.application.receiver;

import cases.Ping2NodesContext;
import global.unet.domain.protocol.ping.PingMessageRequest;
import global.unet.domain.protocol.ping.PingMessageResponse;
import org.junit.Assert;
import org.junit.Test;

//TODO extends UseCase
public class SynchronousMessageBusDriverAdaptorTest {

    private Ping2NodesContext ping2NodesContext;

    public void init (){
        ping2NodesContext = new Ping2NodesContext();
        ping2NodesContext.init();

    }
    //TODO а где тест то?
    @Test
    public void testHandle() {
        init();

        SynchronousMessageBusDriverAdaptor synchronousMessageBusDriverAdaptor
                = new SynchronousMessageBusDriverAdaptor();

        synchronousMessageBusDriverAdaptor.addHandlerMapping(PingMessageRequest.class,
                ping2NodesContext.getPingMessageHandler());

        PingMessageRequest testingRequestMessage = ping2NodesContext.getSourceNodePingMessageRequest();
        PingMessageResponse expectedResponseMessage = ping2NodesContext.getDestinationNodePingMessageResponse();
        synchronousMessageBusDriverAdaptor.handle(testingRequestMessage);

        Assert.assertEquals(
                ping2NodesContext.getListNotifier().getMessages().get(0),
                expectedResponseMessage
        );
    }
}