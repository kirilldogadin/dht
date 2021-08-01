package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.messages.builders.BaseMessageBuilder;
import global.unet.structures.NodeInfo;

import java.util.UUID;

public class Pong extends BaseMessage<Pong> implements MessageType.Response {

    private Pong(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    public static class MessageBuilder extends BaseMessageBuilder<Pong> {

        public MessageBuilder(){
            super();
        }

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
