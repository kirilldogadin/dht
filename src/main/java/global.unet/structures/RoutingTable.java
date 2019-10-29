package global.unet.structures;

import global.unet.id.UnionId;

import java.util.Set;

/**
 * Таблица роутинга. Содержит NodeInfo других нод
 * @see NodeInfo
 * Отвечает за роутинг внутри Union
 *
 * Это именно структура, не сервис, сервис другой
 */
public interface RoutingTable {

    Set<NodeInfo> findClosestUnionIds(UnionId unid);
    void addNode(NodeInfo nodeInfo);

}
