package global.unet.node;

import global.unet.id.NetworkId;
import global.unet.routing.table.NodeInfo;
import global.unet.routing.table.RoutingTable;
import global.unet.server.Server;

import java.util.Set;

public class KademliaRoutingNode implements RoutingNode {

    final Server server;
    //todo должен быть сервис, а не сразу стукртукра
    final RoutingTable routingTable;

    public KademliaRoutingNode(Server server, RoutingTable routingTable) {
        this.server = server;
        this.routingTable = routingTable;
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
        routingTable.addNode(nodeInfo);
    }
}
