package global.unet.service.router;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.Set;

/**
 *  Сервис отвечает за роутинг внутри UnionId
 *
 *
 */
public interface UnidRouter {

    //TODO поменять на SearchResult?
    Set<NodeInfo> findClosestNodes(UnionId unid);

    void addNode(NodeInfo nodeInfo);

}
