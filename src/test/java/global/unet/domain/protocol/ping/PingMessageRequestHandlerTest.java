package global.unet.domain.protocol.ping;

import global.unet.domain.messages.Message;
import org.junit.Test;
import util.fake.messages.PairMessageGenerator;
import util.fake.node.NodeInfoPairGenerator;
import util.fake.notifier.ListNotifierDrivenAdaptor;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static util.UnionGenerator.*;

public class PingMessageRequestHandlerTest {

    private final ListNotifierDrivenAdaptor<Message> listNotifier = new ListNotifierDrivenAdaptor<>();
    private final PingMessageHandler pingMessageHandler = new PingMessageHandler(listNotifier);
    private final NodeInfoPairGenerator nodeInfoPairGenerator = new NodeInfoPairGenerator(constantId(), constantId2());
    private final PairMessageGenerator pairMessageGenerator = new PairMessageGenerator(
            nodeInfoPairGenerator.nodeInfo1(),
            nodeInfoPairGenerator.nodeInfo2()
    );

    @Test
    public void testHandle() {
        PairMessageGenerator.PairMessageContext pairMessageContext = pairMessageGenerator.generatePairMessageContext(UUID.randomUUID(),constantNetworkId());
        PingMessageRequest pingMessageRequest = pairMessageContext.pingMessageRequest();
        PingMessageResponse expectedPingMessageResponse = pairMessageContext.pingMessageResponse();

        pingMessageHandler.handle(pingMessageRequest);

        Message responseMessage = listNotifier.getMessages().get(0);
        assertTrue(responseMessage.equals(expectedPingMessageResponse));
    }
}