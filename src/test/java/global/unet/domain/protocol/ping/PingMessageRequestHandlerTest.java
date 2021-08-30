package global.unet.domain.protocol.ping;

import cases.Ping2NodesContext;
import global.unet.domain.messages.Message;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PingMessageRequestHandlerTest  {

    private Ping2NodesContext ping2NodesContext = new Ping2NodesContext();

    void initBefore (){
        ping2NodesContext.init();
    }
    @Test
    public void testHandle() {
        initBefore();

        PingMessageRequest pingMessageRequest = ping2NodesContext.getSourceNodePingMessageRequest();
        PingMessageResponse expectedPingMessageResponse = ping2NodesContext.getDestinationNodePingMessageResponse();

        ping2NodesContext.getPingMessageHandler().handle(pingMessageRequest);

        Message responseMessage = ping2NodesContext.getListNotifier().getMessages().get(0);
        assertTrue(responseMessage.equals(expectedPingMessageResponse));
    }
}