package global.unet.routing.table;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.node.NodeType;

import java.net.InetAddress;
import java.util.List;

/**
 * Таблица роутинга. Содержит NodeInfo других нод
 * @see NodeInfo
 */
public interface RoutingTable {

    List<NodeInfo> findClosestUnionIds(UnionId unid);
    List<NodeInfo> findClosestUnionIds(NetworkId networkId);
    void addNode(NodeInfo nodeInfo);

    /**
     * Инормация о пире
     *
     * @see NetworkId
     * @see NodeType
     */


}
