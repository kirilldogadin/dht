package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.messages.builders.BaseMessageBuilder;
import global.unet.structures.NodeInfo;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

public class Ping extends BaseMessage implements Serializable, MessageType.Request {

    public Ping(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }


    static class MessageBuilder extends BaseMessageBuilder<Ping> {

        @Override
        public Ping build() {
            return new Ping(
                    source,
                    destination,
                    networkId,
                    messageId,
                    hopes

            );
        }
    }

    @Override
    public String toString() {
        return "Ping{" +
                "messageId=" + messageId +
                ", networkId=" + networkId +
                ", source=" + source +
                ", destination=" + destination +
                ", hopes=" + hopes +
                '}';
    }
}
