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



    //Само поле Hopes финальное, билдер создает дефолтное значение, если не указано другое


    //не глобальный адрес , потому что сообщение не знает о вложенности
    final UnionId networkId;
    final NodeInfo source;

    public MessageBuilder(UnionId networkId, NodeInfo source) {
        this.networkId = networkId;
        this.source = source;
    }

    public InitReq initReq(NodeInfo destination){
        return new InitReq(source,destination,networkId,generateUUID());
    }

    public InitReq initReq(NodeInfo destination,int hopes){
        return new InitReq(source,
                destination,
                networkId,
                generateUUID(),
                hopes);
    }

    public Pong pong(NodeInfo destination){
        return new Pong(
                source,
                destination,
                networkId,
                generateUUID());
    }

    private UUID generateUUID(){
        return UUID.randomUUID();
    }
}
