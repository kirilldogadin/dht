package global.unet.service;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

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
     * //TODO возвращаемый тип
     * @param unid contentId
     * @return
     */
    SearchResult contentLookup(UnionId unid);

    void addNode(NodeInfo nodeInfo);

    /**
     * обработка ноового контента
     */
    //void contentInfo();


}
