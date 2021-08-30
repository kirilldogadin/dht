package util.fake.messages;

import global.unet.domain.id.UnionId;
import global.unet.domain.protocol.ping.PingMessageRequest;
import global.unet.domain.protocol.ping.PingMessageResponse;
import global.unet.domain.structures.NodeInfo;

import java.util.UUID;

/**
 * Содержит контекст пар сообщений на основе терминологии useCasePair иточник-назначение
 */
public abstract class BaseMessagePairCaseContext {

    protected final NodeInfo source;
    protected final NodeInfo destination;
    protected UUID messageUUID;
    protected UnionId networkId;

    public BaseMessagePairCaseContext(NodeInfo source, NodeInfo destination, UUID messageUUID, UnionId networkId) {
        this.source = source;
        this.destination = destination;
        this.messageUUID = messageUUID;
        this.networkId = networkId;
    }

}
