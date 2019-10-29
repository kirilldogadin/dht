package global.unet.service;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;
import global.unet.structures.RoutingTable;

import java.util.Set;

public class KadUnidRouter implements UnidRouter {

    private final RoutingTable routingTable;

    public KadUnidRouter(RoutingTable routingTable) {
        this.routingTable = routingTable;
    }

    @Override
    public Set<NodeInfo> findClosestNodes(UnionId unid) {
       return routingTable.findClosestNodes(unid);
    }

    @Override
    public void addNode(NodeInfo nodeInfo) {
        routingTable.addNode(nodeInfo);
    }
}
