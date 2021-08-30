package cases;

import global.unet.domain.protocol.ping.PingMessageHandler;
import global.unet.domain.protocol.ping.PingMessageRequest;
import global.unet.domain.protocol.ping.PingMessageResponse;
import util.fake.messages.PingMessagePairCaseContext;

import java.util.UUID;

public class Ping2NodesContext extends Base2NodesUseCaseContext {

    private PingMessageHandler pingMessageHandler;
    private PingMessageRequest sourceNodePingMessageRequest;
    private PingMessageResponse destinationNodePingMessageResponse;

    @Override
    public void init(){
        super.init();
        pingMessageHandler = new PingMessageHandler(listNotifier);

        //Todo в Fake Class
        PingMessagePairCaseContext pingMessagePairCaseContext = newRandomPingPairMessagesContext();

        sourceNodePingMessageRequest = pingMessagePairCaseContext.sourceNodePingMessageRequest();
        destinationNodePingMessageResponse = pingMessagePairCaseContext.destinationNodePingMessageResponse();

    }

    private PingMessagePairCaseContext newRandomPingPairMessagesContext() {
        return new PingMessagePairCaseContext(
                nodeInfoPairGenerator.sourceNodeInfo(),
                nodeInfoPairGenerator.destinationNodeInfo(),
                UUID.randomUUID(), networkId);
    }

    public PingMessageHandler getPingMessageHandler() {
        return pingMessageHandler;
    }

    //TODO дубли кода
    public PingMessageRequest getSourceNodePingMessageRequest() {
        return sourceNodePingMessageRequest;
    }

    public PingMessageResponse getDestinationNodePingMessageResponse() {
        return destinationNodePingMessageResponse;
    }
}
