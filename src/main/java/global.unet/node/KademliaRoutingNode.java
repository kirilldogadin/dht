package global.unet.node;

import global.unet.id.NetworkId;
import global.unet.server.Server;
import global.unet.service.UnidRouter;
import global.unet.structures.NodeInfo;

import java.util.Set;

/**
 * Нода сервис которая выполняет только роутинг. Добавление нод
 */
public class KademliaRoutingNode implements RoutingNode {

    final Server server;
    //todo должен быть сервис, а не сразу стукртукра
    final UnidRouter unidRouter;

    public KademliaRoutingNode(Server server, UnidRouter unidRouter) {
        this.server = server;
        this.unidRouter = unidRouter;
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void shutDown() {

    }


    @Override
    public Set<NodeInfo> findClosestNode(NetworkId networkId) {
        throw new RuntimeException("method not implement yet");
    }

    @Override
    public void addNode(NodeInfo nodeInfo) {
        unidRouter.addNode(nodeInfo);
    }
}
