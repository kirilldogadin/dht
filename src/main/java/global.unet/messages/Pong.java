package global.unet.messages;

import global.unet.id.NodeInfoHolder;
import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

public class Pong extends BaseMessage<Pong> implements MessageType.Response {

    private Pong(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    public static class Builder extends BaseBuilder<Pong> {

        Builder(NodeInfoHolder nodeInfoHolder){
            super(nodeInfoHolder, Pong.class);
        }

        @Override
        Pong finalBuild() {
            return new Pong(
                    source,
                    destination,
                    networkId,
                    messageId,
                    hopes

            );
        }
    }

    public interface BuilderFabric extends MessageBuilderFabric<Pong, Pong.Builder> {
    }

    public static class BuilderFabricImpl implements BuilderFabric {

        final NodeInfoHolder nodeInfoHolder;

        public BuilderFabricImpl(NodeInfoHolder nodeInfoHolder) {
            this.nodeInfoHolder = nodeInfoHolder;
        }

        @Override
        public Pong.Builder builder() {
            return new Builder(nodeInfoHolder);
        }
    }
}
