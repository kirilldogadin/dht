package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

public class Ping extends BaseMessage {

    public Ping(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    public Ping(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId) {
        super(source, destination, networkId, messageId);
    }
}
