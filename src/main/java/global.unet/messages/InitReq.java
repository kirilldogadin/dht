package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * Запрос на добавление себя
 * по идее это базовые сообщение которое содержит информацию о ноде отправителе
 */
public class InitReq extends BaseMessage {

    private InitReq(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    //TOdo подумать мб передавать тип класса и тп
    // https://habr.com/ru/company/jugru/blog/438866/
    public static Builder builder() {
        return new Builder();
    }


    //Todo подумать как вынести в Base
    public static class Builder {
        private UUID messageId;
        private UnionId networkId;
        private NodeInfo source;
        private NodeInfo destination;
        private int hopes = HOPES_DEFAULT;

        Builder setMessageId(UUID messageId) {
            this.messageId = messageId;
            return this;
        }

        Builder setNetworkId(UnionId networkId) {
            this.networkId = networkId;
            return this;
        }

        Builder setSource(NodeInfo source) {
            this.source = source;
            return this;
        }

        public Builder setDestination(NodeInfo destination) {
            this.destination = destination;
            return this;
        }

        public Builder setHopes(int hopes) {
            this.hopes = hopes;
            return this;
        }

        InitReq build() {
            return new InitReq(
                    source,
                    destination,
                    networkId,
                    messageId,
                    hopes);
        }
    }
}
