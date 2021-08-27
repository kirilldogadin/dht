package util.fake.messages;

import global.unet.domain.id.UnionId;
import global.unet.domain.protocol.ping.PingMessageRequest;
import global.unet.domain.protocol.ping.PingMessageResponse;
import global.unet.domain.structures.NodeInfo;

import java.util.UUID;

/**
 * Генерирует сообщения на основе отношения иточник-назначение
 */
public class PairMessageGenerator {

    private final NodeInfo source;
    private final NodeInfo destination;

    public PairMessageGenerator(NodeInfo source, NodeInfo destination) {
        this.source = source;
        this.destination = destination;
    }

    public PairMessageContext generatePairMessageContext(UnionId networkId){
        return generatePairMessageContext(UUID.randomUUID(), networkId);
    }
    /**
     * Содержит связанную пару сообщений, общим
     * UUID ответа запроса
     * networkId UnionId
     * sessionId и прочими вещами
     * @param networkId
     */

    public PairMessageContext generatePairMessageContext(UUID messageUUID, UnionId networkId){
        return new PairMessageContext(messageUUID, networkId);
    }

    /**
     * Содержит общие
     * messageUUID networkId для пары сообщений
     */
    public class PairMessageContext {
        final UUID messageUUID;
        private final UnionId networkId;

        private PairMessageContext(UUID messageUUID, UnionId networkId) {
            this.messageUUID = messageUUID;
            this.networkId = networkId;
        }

        public PingMessageRequest pingMessageRequest() {
            return new PingMessageRequest(source, destination, networkId, messageUUID);
        }

        public PingMessageResponse pingMessageResponse() {
            return new PingMessageResponse(destination, source, networkId, messageUUID);
        }
    }
}
