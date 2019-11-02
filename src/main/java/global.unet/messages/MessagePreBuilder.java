package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * создает сообщения , а также отвечает за генерацию ID сообщения
 * Для одного юниона работает
 */
public class MessagePreBuilder {


    //TODO локальные классы - можно сделать лямбдами?
    //Само поле Hopes финальное, билдер создает дефолтное значение, если не указано другое


    //Todo а вот IP может
    // меняться
    // быть истинно видным только снаружи (за натом
    //не глобальный адрес , потому что сообщение не знает о вложенности
    final UnionId networkId;
    final NodeInfo source;


    public MessagePreBuilder(UnionId networkId, NodeInfo source) {
        this.networkId = networkId;
        this.source = source;
    }

    //старый метод, теперь не актуален, только для сравнения "было/стало"
    //TODO для написания статьи история одного рефакторинга
    public Pong pong(Pong.PongBuilder pongBuilder) {
        return pongBuilder
                .setSource(source)
                .setNetworkId(networkId)
                .setMessageId(UUID.randomUUID())
                .build();
    }

    //TODO суть метода в том , чтобы получить билдер для конретного типа,
    // чтобы заполнить в нем Пребилдер и получить билдер, который используется
    // БЕЗ передачи в него билдера

    public <T extends Message> BaseMessage.BaseBuilder<T> builder(Buildable<T> buildable){
        return buildable.builder(this::fillMessageAsResponse);
    }


    /**
     * Заполняет
     * setSource(source)
     * networkId
     * UUID -> randomUUID Если он не задан
     *
     */
    public <T extends Message> T createFullMessage(BaseMessage.BaseBuilder<T> pongBuilder) {
        return pongBuilder.messageId == null
                ? createFullMessageRequest(pongBuilder)
                : createFullMessageResponse(pongBuilder);
    }

    /**
     * Заполняет
     * setSource(source)
     * networkId
     * UUID -> randomUUID
     * Подразумевается, что это сообщение инициатор. В котором будет сгенерировано idсобщения
     * //TODO id сообщения на самом деле id сессии общения же?
     * @param pongBuilder
     * @param <T>
     * @return
     */
    public <T extends Message> T createFullMessageRequest(BaseMessage.BaseBuilder<T> pongBuilder) {
        return fillBaseFieldsOfMsg(pongBuilder)
                .setMessageId(UUID.randomUUID())
                .build();
    }

    /**
     * Сохраняет uuid исходного сообщения переданного в pongBuilder
     *
     * @param pongBuilder билдер сообщения типа <T>
     * @param <T>         тип сообщения
     * @return
     */
    public <T extends Message> T createFullMessageResponse(BaseMessage.BaseBuilder<T> pongBuilder) {
        return fillBaseFieldsOfMsg(pongBuilder)
                .build();
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
