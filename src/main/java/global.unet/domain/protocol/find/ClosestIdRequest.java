package global.unet.domain.protocol.find;

import global.unet.domain.id.UnionId;
import global.unet.domain.messages.BaseMessageWithResource;
import global.unet.domain.structures.NodeInfo;

import java.util.UUID;

/**
 * Запрос на поиск ближайшнего к Id ноды
 */
public class ClosestIdRequest extends BaseMessageWithResource {

    private ClosestIdRequest(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource) {
        super(source, destination, networkId, messageId, hopes, resource);
    }

}
