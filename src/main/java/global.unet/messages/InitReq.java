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
    // https://gist.github.com/lerouxrgd/87c1f71ba6a447c6311d172fe61c2924

    //по идее определение в подтипе вполне должно устроить
    //то есть не надо городить асбтрактный метод, потому что обращение все равно идет черех конктретный класс
    //это имело бы смысл если бы мы обращались через не знаю какой подтип
    public static Builder builder() {
        return new Builder();
    }

    //Todo подумать как вынести в Base
    static class Builder extends BaseBuilder<InitReq> {

        @Override
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
