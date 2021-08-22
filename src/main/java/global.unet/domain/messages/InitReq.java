package global.unet.domain.messages;

import global.unet.domain.id.UnionId;
import global.unet.domain.structures.NodeInfo;

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
    // https://gist.github.com/lerouxrgd/87c1f71ba6a447c6311d172fe61c2924

    //Todo подумать как вынести в Base
}
