package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

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

    public static ResourceResponse.Builder builder(Consumer<BaseBuilder<ResourceResponse>> preBuilder) {
        return new Builder(preBuilder);
    }

    public static boolean isReq() {
        return false;
    }

    public static class Builder extends BaseMessageWithResource.Builder<ResourceResponse> {

        Set<NodeInfo> nodeInfos;

        Builder(Consumer<BaseBuilder<ResourceResponse>> preBuilder) {
            super(preBuilder);
        }

        public ResourceResponse.Builder setNodeInfos(Set<NodeInfo> nodeInfos) {
            this.nodeInfos = nodeInfos;
            return this;
        }

        @Override
        ResourceResponse finalBuild() {
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

    public interface BuilderFabric extends MessageBuilderFabric<ResourceResponse,ResourceResponse.Builder>{

    }


}
