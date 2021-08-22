package global.unet.domain.protocol.ping;

import global.unet.domain.id.UnionId;
import global.unet.domain.messages.*;
import global.unet.domain.structures.NodeInfo;

import java.util.UUID;

public class PingResponse extends BaseMessage
{

    private PingResponse(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    public PingResponse(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId) {
        this(source, destination, networkId, messageId, HOPES_DEFAULT);
    }

    @Override
    public String toString() {
        return "PingResponse{" +
                "messageId=" + messageId +
                ", networkId=" + networkId +
                ", source=" + source +
                ", destination=" + destination +
                ", hopes=" + hopes +
                '}';
    }
}
