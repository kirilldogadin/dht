package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;
import java.util.function.Consumer;

public class Pong extends BaseMessage<Pong> {

    private Pong(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    static BaseBuilder<Pong> builder(Consumer<BaseBuilder<Pong>> preBuilder) {
        return new PongBuilder(preBuilder);
    }


    static class PongBuilder extends BaseBuilder<Pong> {

        PongBuilder(Consumer<BaseBuilder<Pong>> preBuilder) {
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
}
