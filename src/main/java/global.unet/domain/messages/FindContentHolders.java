package global.unet.domain.messages;

import global.unet.domain.id.UnionId;
import global.unet.domain.structures.NodeInfo;

import java.util.UUID;

public class FindContentHolders extends BaseMessageWithResource {

    public FindContentHolders(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource) {
        super(source, destination, networkId, messageId, hopes, resource);
    }

    public FindContentHolders(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, UnionId resource) {
        super(source, destination, networkId, messageId, resource);
    }
}
