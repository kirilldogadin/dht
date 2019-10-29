package global.unet.service;

import global.unet.id.UnionId;
import global.unet.routing.table.NodeInfo;
import global.unet.routing.table.RoutingTable;

import java.util.Set;

public class KadUnidRouter implements UnidRouter {

    private final RoutingTable routingTable;

    public KadUnidRouter(RoutingTable routingTable) {
        this.routingTable = routingTable;
    }

    @Override
    public Set<NodeInfo> findClosestUnionIds(UnionId unid) {
       return routingTable.findClosestUnionIds(unid);
    }

    @Override
    public void addNode(NodeInfo nodeInfo) {
        routingTable.addNode(nodeInfo);

    }
}
