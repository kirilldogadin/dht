package global.unet.service;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.HashMap;
import java.util.Set;

public class KadContentRouter implements ContentRouter {

    private final KadUnidRouter unidRouter;
    private final HashMap<UnionId,Set<UnionId>> holdersByContentId = new HashMap<>();

    public KadContentRouter(KadUnidRouter unidRouter) {
        this.unidRouter = unidRouter;
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
    public void addNode(NodeInfo nodeInfo) {

    }
}
