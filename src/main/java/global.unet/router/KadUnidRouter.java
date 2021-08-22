package global.unet.router;

import global.unet.domain.id.UnionId;
import global.unet.domain.structures.NodeInfo;
import global.unet.domain.structures.RoutingTable;
import global.unet.domain.structures.XorTreeRoutingTable;

import java.util.Set;

/**
 * Сервис который отвечает за роутинг внутри Union
 */
public class KadUnidRouter implements UnidRouter {

    private final RoutingTable routingTable;

    public KadUnidRouter(UnionId selfUnionId){
        this.routingTable = new XorTreeRoutingTable(selfUnionId);
    }

    public KadUnidRouter(UnionId selfUnionId, int capacity){
        this.routingTable = new XorTreeRoutingTable(selfUnionId, capacity);
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
