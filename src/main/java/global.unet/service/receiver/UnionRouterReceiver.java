package global.unet.service.receiver;

import global.unet.messages.*;
import global.unet.service.router.UnidRouter;
import global.unet.structures.NodeInfo;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Логика работы с принимаемыми сообщениями
 *
 * UnionRouter в названниии означает, что только сообщения только с роутингом
 */
public class UnionRouterReceiver implements Receiver {

    //TOdo id и прочее

    //делать через билдеры

    protected final UnidRouter unidRouter;
    protected final Consumer<Message> messageSender;
    protected final MessageBuilder messageBuilder;

    public UnionRouterReceiver(UnidRouter unidRouter, Consumer<Message> messageSender, MessageBuilder messageBuilder) {
        this.unidRouter = unidRouter;
        this.messageSender = messageSender;
        this.messageBuilder = messageBuilder;
    }

    public void handle(Message message){
        //тут свитч на тип и выбор далее
    }

    /**
     *
     * @param closestIdReq
     * @return
     */
    public Set<NodeInfo> handle(ClosestIdReq closestIdReq){
        unidRouter.addNode(closestIdReq.getSource());
        return unidRouter.findClosestNodes(closestIdReq.getResource());
    }

    public void handle(InitReq initReq){
        unidRouter.addNode(initReq.getSource());

    }

    public void handle(UnionBootstrap unionBootstrap) {
        //Взять помимо ближайших из NodeHolder инормацию по всем ближайшим нодам, а также рейтинговым
        //послать сообщение
        messageSender.accept(null);
    }

    public void handle(Ping ping){
        Pong pong = messageBuilder.pong(ping.getDestination());
        messageBuilder.initReq(
                InitReq.builder()
                .setDestination(ping.getDestination()));
        messageSender.accept(pong);

    }

    public void handle(Pong pong){

    }



}
