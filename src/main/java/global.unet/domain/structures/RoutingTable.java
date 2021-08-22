package global.unet.domain.structures;

import global.unet.domain.id.UnionId;

import java.util.Set;

//TODO дженерифицировать возвращаемое значение

/**
 * Таблица роутинга. Содержит NodeInfo других нод
 * @see NodeInfo
 * Отвечает за роутинг внутри Union
 *
 * Это именно структура, не сервис, сервис другой
 */
public interface RoutingTable {

    Set<NodeInfo> findClosestNodes(UnionId unid);
    void addNode(NodeInfo nodeInfo);

}
