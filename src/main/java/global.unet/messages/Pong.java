package global.unet.messages;

import global.unet.id.NodeInfoHolder;
import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;
import java.util.function.Consumer;

public class Pong extends BaseMessage<Pong> {

    private Pong(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    static Pong.Builder builder(Consumer<BaseBuilder<Pong>> preBuilder) {
        return new Builder(preBuilder);
    }

    public static class Builder extends BaseBuilder<Pong> {

        Builder(Consumer<BaseBuilder<Pong>> preBuilder) {
            super(preBuilder);
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

        final CommonFieldBuilder commonFieldBuilder;

        public BuilderFabricImpl(NodeInfoHolder nodeInfoHolder) {
            this.commonFieldBuilder = new CommonFieldBuilder(nodeInfoHolder);
        }

        @Override
        public Pong.Builder builder() {
            return Pong.builder(commonFieldBuilder::fillMessageAsResponse);
        }
    }
}
