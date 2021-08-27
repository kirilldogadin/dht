package global.unet.domain.protocol.ping;

import global.unet.domain.id.UnionId;
import global.unet.domain.messages.*;
import global.unet.domain.structures.NodeInfo;

import java.io.Serializable;
import java.util.UUID;

public class PingMessageRequest extends BaseMessage implements Serializable {

    public PingMessageRequest(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    public PingMessageRequest(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId) {
        super(source, destination, networkId, messageId);
    }


    @Override
    public String toString() {
        return "Ping{" +
                "messageId=" + messageId +
                ", networkId=" + networkId +
                ", source=" + source +
                ", destination=" + destination +
                ", hopes=" + hopes +
                '}';
    }


}
