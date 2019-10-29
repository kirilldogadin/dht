package global.unet.service.Receiver;

import global.unet.messages.ClosestIdReq;
import global.unet.messages.Message;
import global.unet.service.KadUnidRouter;
import global.unet.structures.NodeInfo;

import java.util.Set;

/**
 * Логика работы с принимаемыми сообщениями
 */
public class RoutingReceiver implements Receiver {

    private final KadUnidRouter unidRouter;

    public RoutingReceiver(KadUnidRouter unidRouter) {
        this.unidRouter = unidRouter;
    }

    public void handle(Message message){

    }

    /**
     *
     * @param closestIdReq
     * @return
     */
    public Set<NodeInfo> handle(ClosestIdReq closestIdReq){
         return unidRouter.findClosestNodes(closestIdReq.getUnionId());



    }

}
