package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * Запрос на поиск ближайшнего к Id ноды
 */
public class ClosestIdReq extends BaseMessageWithResource {

    private ClosestIdReq(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource) {
        super(source, destination, networkId, messageId, hopes, resource);
    }

}
