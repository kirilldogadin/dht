package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;

/**
 * создает сообщения , а также отвечает за генерацию ID сообщения
 */
public class MessageBuilder {


    //TODO реализовать билдер для создания сообщений, в котором можно как
    // указывать HOPES так и нет, чтобы не плодить кучу методв с HOPES  и без


    //TODO локальные классы - можно сделать лямбдами?
    //Само поле Hopes финальное, билдер создает дефолтное значение, если не указано другое

    //не глобальный адрес , потому что сообщение не знает о вложенности
    final UnionId networkId;
    final NodeInfo source;

    //Todo а вот IP может
    // меняться
    // быть истинно видным только снаружи (за натом
    public MessageBuilder(UnionId networkId, NodeInfo source) {
        this.networkId = networkId;
        this.source = source;
    }

    //старый метод, теперь не актуален, только для сравнения "было/стало"
    public Pong pong(Pong.PongBuilder pongBuilder){
        return pongBuilder
                .setSource(source)
                .setNetworkId(networkId)
                .setMessageId(UUID.randomUUID())
                .build();
    }

     public <T extends Message> T fillMessage(BaseMessage.BaseBuilder<T> pongBuilder){
        return pongBuilder
                .setSource(source)
                .setNetworkId(networkId)
                .setMessageId(UUID.randomUUID())
                .build();
    }

    private UUID generateUUID(){
        return UUID.randomUUID();
    }
}
