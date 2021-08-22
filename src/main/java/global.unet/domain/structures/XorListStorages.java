package global.unet.domain.structures;

import global.unet.domain.id.UnionId;

import java.util.Set;

/**
 * другая структура, которая хранит данные не в бакетах
 * хранит просто упорядочнный по дистанции, где selfUnid в конце
 * как в статье
 */
public class XorListStorages implements RoutingTable {

    @Override
    public Set<NodeInfo> findClosestNodes(UnionId unid) {
        return null;
    }

    @Override
    public void addNode(NodeInfo nodeInfo) {

    }
}
