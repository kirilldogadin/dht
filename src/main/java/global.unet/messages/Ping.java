package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.io.Serializable;
import java.util.UUID;

public class Ping extends BaseMessage implements Serializable {

    public Ping(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    public static BaseBuilder<Ping> builder() {
        return new Builder();
    }

    static class Builder extends BaseBuilder<Ping> {

        @Override
        Ping build() {
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
