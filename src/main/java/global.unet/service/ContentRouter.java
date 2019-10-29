package global.unet.service;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.Set;

/**
 *  Сервис отвечает за роутинг внутри UnionId
 *
 *
 */
public interface ContentRouter {

    /**
     *
     * Метод может вернуть
     *
     * 1. Список владельцев контента
     * Или
     * 2. Список ближайших нод
     * Нужен какой-то флаг? ИЛИ NodeInfo это ключ, в
     * Мапе, значение в которой контент , который есть у ноды?
     *
     * @param unid contentId
     * @return
     */
    Set<NodeInfo> findClosestUnionIds(UnionId unid);

    void addNode(NodeInfo nodeInfo);

}
