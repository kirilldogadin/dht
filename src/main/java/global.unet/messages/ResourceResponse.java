package global.unet.messages;

import global.unet.id.NodeInfoHolder;
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
public class ResourceResponse extends BaseMessageWithResource implements MessageType.Response {

    final Set<NodeInfo> nodeInfos;

    private ResourceResponse(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource, Set<NodeInfo> nodeInfos) {
        super(source, destination, networkId, messageId, hopes, resource);
        this.nodeInfos = nodeInfos;
    }

    //Todo удалить
    public static ResourceResponse.Builder builder(Consumer<BaseBuilder<ResourceResponse>> preBuilder) {
        return new Builder(preBuilder);
    }

    public static ResourceResponse.Builder builder(NodeInfoHolder nodeInfoHolder) {
        return new Builder(nodeInfoHolder);
    }

//    public static ResourceResponse.Builder builder(NodeInfoHolder nodeInfoHolder) {
//        return new Builder(new CommonFieldBuilder(nodeInfoHolder));
//    }

    public static class Builder extends BaseMessageWithResource.Builder<ResourceResponse> {

        Set<NodeInfo> nodeInfos;


        Builder(Consumer<BaseBuilder<ResourceResponse>> preBuilder) {
            super(preBuilder);
        }

        public Builder(NodeInfoHolder nodeInfoHolder){
            super(nodeInfoHolder, ResourceResponse.class);

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
