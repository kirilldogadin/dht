package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.messages.builders.BaseMessageBuilder;
import global.unet.messages.builders.MessageBuilderFabric;
import global.unet.structures.NodeInfo;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Обычно ответ на сообщение с сообщение с ресурсом, такие как
 * поиск ноды
 * поиск контента
 */
public class ResourceResponse extends BaseMessageWithResource implements MessageType.Response {

    final Set<NodeInfo> nodeInfos;

    private ResourceResponse(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource, Set<NodeInfo> nodeInfos) {
        super(source, destination, networkId, messageId, hopes, resource);
        this.nodeInfos = nodeInfos;
    }

//    public static ResourceResponse.Builder builder(NodeInfoHolder nodeInfoHolder) {
//        return new Builder(new CommonFieldBuilder(nodeInfoHolder));
//    }

    public static class MessageBuilder extends BaseMessageWithResource.MessageBuilder<ResourceResponse> {

        Set<NodeInfo> nodeInfos;

        public MessageBuilder setNodeInfos(Set<NodeInfo> nodeInfos) {
            this.nodeInfos = nodeInfos;
            return this;
        }

        @Override
        public ResourceResponse build() {
            return new ResourceResponse(
                    source,
                    destination,
                    networkId,
                    messageId,
                    hopes,
                    resource,
                    nodeInfos);
        }
    }

}
