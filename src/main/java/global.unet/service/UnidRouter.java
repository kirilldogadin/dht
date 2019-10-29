package global.unet.service;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.Set;

/**
 *  Сервис отвечает за роутинг внутри UnionId
 *
 *
 */
public interface UnidRouter {

    Set<NodeInfo> findClosestUnionIds(UnionId unid);

    void addNode(NodeInfo nodeInfo);

}
