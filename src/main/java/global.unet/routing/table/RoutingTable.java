package global.unet.routing.table;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.node.NodeType;

import java.util.Set;

/**
 * Таблица роутинга. Содержит NodeInfo других нод
 * @see NodeInfo
 */
public interface RoutingTable {

    Set<NodeInfo> findClosestUnionIds(UnionId unid);
    Set<NodeInfo> findClosestUnionIds(NetworkId networkId);
    void addNode(NodeInfo nodeInfo);

    /**
     * Инормация о пире
     *
     * @see NetworkId
     * @see NodeType
     */


}
