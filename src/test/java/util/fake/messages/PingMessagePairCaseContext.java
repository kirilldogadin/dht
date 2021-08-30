package util.fake.messages;

import global.unet.domain.id.UnionId;
import global.unet.domain.protocol.ping.PingMessageRequest;
import global.unet.domain.protocol.ping.PingMessageResponse;
import global.unet.domain.structures.NodeInfo;

import java.util.UUID;

/**
 * Содержит контекст пар сообщений на основе терминологии useCasePair иточник-назначение
 */
public class PingMessagePairCaseContext extends BaseMessagePairCaseContext {

    public PingMessagePairCaseContext(NodeInfo source, NodeInfo destination, UUID messageUUID, UnionId networkId) {
        super(source, destination, messageUUID, networkId);
    }

    /**
     * @return PingMessageRequest filled as SourceNode as source and DestinationNode as Destination
     */
    public PingMessageRequest sourceNodePingMessageRequest() {
        return new PingMessageRequest(source, destination, networkId, messageUUID);
    }

    /**
     * @return PingMessageResponse filled as DestinationNode(Class Context Name) as source and SourceNode(Class Context Name) as Destination
     */
    public PingMessageResponse destinationNodePingMessageResponse() {
        return new PingMessageResponse(destination, source, networkId, messageUUID);
    }


}
