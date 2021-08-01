package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.messages.builders.BaseMessageBuilder;
import global.unet.structures.NodeInfo;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Запрос на поиск ближайшнего к Id ноды
 */
public class ClosestIdReq extends BaseMessageWithResource {

    private ClosestIdReq(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource) {
        super(source, destination, networkId, messageId, hopes, resource);
    }


    private static class MessageBuilder extends BaseMessageWithResource.MessageBuilder<ClosestIdReq> {


        @Override
        public ClosestIdReq build() {
            return new ClosestIdReq(
                    this.source,
                    this.destination,
                    this.networkId,
                    this.messageId,
                    this.hopes,
                    this.resource);
        }


    }


}
