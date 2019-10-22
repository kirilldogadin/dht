package global.unet.routing.table;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;

import java.util.List;

/**
 * другая структура, которая хранит данные не в бакетах
 * хранит просто упорядочнный по дистанции, где selfUnid в конце
 * как в статье
 */
public class XorListStorages implements RoutingTable {

    @Override
    public List<NodeInfo> findClosestUnionIds(UnionId unid) {
        return null;
    }

    @Override
    public List<NodeInfo> findClosestUnionIds(NetworkId networkId) {
        return null;
    }

    @Override
    public void addNode(NodeInfo nodeInfo) {

    }
}
