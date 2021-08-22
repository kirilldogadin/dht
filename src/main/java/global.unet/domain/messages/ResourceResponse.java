package global.unet.domain.messages;

import global.unet.domain.id.UnionId;
import global.unet.domain.structures.NodeInfo;

import java.util.Set;
import java.util.UUID;

/**
 * Обычно ответ на сообщение с сообщение с ресурсом, такие как
 * поиск ноды
 * поиск контента
 */
public class ResourceResponse extends BaseMessageWithResource {

    final Set<NodeInfo> nodeInfos;

    private ResourceResponse(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource, Set<NodeInfo> nodeInfos) {
        super(source, destination, networkId, messageId, hopes, resource);
        this.nodeInfos = nodeInfos;
    }
    public ResourceResponse(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, UnionId resource, Set<NodeInfo> nodeInfos) {
        super(source, destination, networkId, messageId, resource);
        this.nodeInfos = nodeInfos;
    }

}
