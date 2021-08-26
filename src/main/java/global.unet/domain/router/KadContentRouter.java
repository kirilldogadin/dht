package global.unet.domain.router;

import global.unet.domain.id.UnionId;
import global.unet.application.SearchResult;

import java.util.HashMap;
import java.util.Set;

//TODO сделать отдельную таблицу в которой хранить ВСЕ unid известные Ноде, чтобы пинговать их и тд, тогда
//TODO тогда структура данных ничего не будет знать о NodeInfo

/**
 * Отвечает за обработку запросов связанных с контентом
 */
public class KadContentRouter extends KadUnidRouter implements ContentRouter {

    //держатели контента, по id контента
    //TODO создать тип значение по аналогии с NodeInfo = ContentInfo
    private final HashMap<UnionId,Set<UnionId>> holdersByContentId = new HashMap<>();

    public KadContentRouter(UnionId selfUnionId) {
        super(selfUnionId);
    }

    public KadContentRouter(UnionId selfUnionId, int capacity) {
        super(selfUnionId, capacity);
    }

    @Override
    public SearchResult contentLookup(UnionId unid) {
        //1. Проверить в списке авторов получить их unids
        //2. По данным unids найти в unidRouter NodeInfo
        //3. Если в unidRouter нет записей, то отдать Gunids список? а есть ли смысл в нем? то есть это те, кто не онлацн
        //как вообще попасть в этот список? Его зона отвестенности = зона ответсвенности за контент,
        //соотсвественно там Ноды, которые скорее всего хранят похожий контент
        //КРОМЕ НОД , которые авторы, их unid скорее всего не совпадет

        //Простой алгоритм, если у меня нет, то вот список ближайших не подойдет,
        // т.к. там может не быть автора, или нод с непохожими id (тех, кто скачал)
        Set<UnionId> unionIds = holdersByContentId.get(unid);
        //Todo isEmptry
        if (!(unionIds != null)){
        }
                throw new RuntimeException("Метод не реализован, простите");

    }

    @Override
    public void addContentInfo(UnionId unionId, Set<UnionId> unionIds) {
        holdersByContentId.put(unionId, unionIds);
    }
}
