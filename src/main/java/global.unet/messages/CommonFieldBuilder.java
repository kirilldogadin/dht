package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * создает сообщения , а также отвечает за генерацию ID сообщения
 * Для одного юниона работает
 */
public class CommonFieldBuilder {

    //Todo а вот IP может
    // меняться
    // быть истинно видным только снаружи (за натом
    //не глобальный адрес , потому что сообщение не знает о вложенности
    final UnionId networkId;
    final NodeInfo source;

    public CommonFieldBuilder(UnionId networkId, NodeInfo source) {
        this.networkId = networkId;
        this.source = source;
    }

    /**
     * Заполняет
     * setSource(source)
     * networkId
     * UUID -> randomUUID
     * Подразумевается, что это сообщение инициатор. В котором будет сгенерировано idсобщения
     * //TODO id сообщения на самом деле id сессии общения же?
     * @param <T>
     * @param pongBuilder
     * @return
     */
    public <T extends Message> BaseMessage.BaseBuilder<T> fillMessageAsRequest(BaseMessage.BaseBuilder<T> pongBuilder) {
        return fillBaseFieldsOfMsg(pongBuilder)
                .setMessageId(generateUUID());
    }

    public <T extends Message> void fillMessageAsResponse(BaseMessage.BaseBuilder<T> pongBuilder) {
        fillBaseFieldsOfMsg(pongBuilder);
    }

    /**
     * Заполняет
     * source
     * networkId
     *
     * @param pongBuilder
     * @param <T>
     * @return
     */
    private <T extends Message> BaseMessage.BaseBuilder<T> fillBaseFieldsOfMsg(BaseMessage.BaseBuilder<T> pongBuilder) {
        return pongBuilder
                .setSource(source)
                .setNetworkId(networkId);
    }

    private UUID generateUUID() {
        return UUID.randomUUID();
    }
}
