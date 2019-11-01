package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

public class Pong extends BaseMessage<Pong> {

    private Pong(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    //TODO нет своих полей
    public static BaseBuilder<Pong> builder() {
        return new PongBuilder();
    }

    static class PongBuilder extends BaseBuilder<Pong> {

        @Override
        Pong build() {
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
