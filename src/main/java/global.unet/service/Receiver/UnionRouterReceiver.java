package global.unet.service.Receiver;

import global.unet.messages.*;
import global.unet.service.UnidRouter;
import global.unet.structures.NodeInfo;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Логика работы с принимаемыми сообщениями
 */
public class UnionRouterReceiver implements Receiver {

    //TOdo id и прочее

    protected final UnidRouter unidRouter;
    protected final Consumer<Message> messageSender;

    public UnionRouterReceiver(UnidRouter unidRouter, Consumer<Message> messageSender) {
        this.unidRouter = unidRouter;
        this.messageSender = messageSender;
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

    }

    public void handle(Pong pong){

    }



}
