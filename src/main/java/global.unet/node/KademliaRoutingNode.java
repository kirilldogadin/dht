package global.unet.node;

import global.unet.id.NetworkId;
import global.unet.routing.table.NodeInfo;
import global.unet.routing.table.RoutingTable;
import global.unet.server.Server;

import java.util.Set;

public class KademliaRoutingNode implements RoutingNode {

    final Server server;
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
        return routingTable.findClosestUnionIds(networkId);
    }

    @Override
    public void addNode(NodeInfo nodeInfo) {
        routingTable.addNode(nodeInfo);
    }
}
