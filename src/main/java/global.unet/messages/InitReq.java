package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * Запрос на добавление себя
 * по идее это базовые сообщение которое содержит информацию о ноде отправителе
 */
public class InitReq extends BaseMessage {

    public InitReq(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes) {
        super(source, destination, networkId, messageId, hopes);
    }

    public InitReq(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId) {
        super(source, destination, networkId, messageId);
    }
}
