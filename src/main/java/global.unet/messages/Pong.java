package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

public class Pong extends BaseMessage<Pong> {

    public Pong(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    public Pong(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId) {
        super(source, destination, networkId, messageId);
    }

    //@Override
    public Builder<Pong> builder1() {
        return new PongBaseBuilder();
    }




    public static class PongBaseBuilder extends BaseBuilder<Pong> {

        @Override
        public Pong build() {
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
