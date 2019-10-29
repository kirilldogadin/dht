package global.unet.node;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.service.UnameResolver;
import global.unet.storage.Content;
import global.unet.structures.NodeInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class KademliaRegularNode implements RegularNode {

    private final UnionId selfUnionId;
    private final RoutingNode routingNode;
    private final StorageNode storageNode;
    private final int tresholdResponsibility;
    private final UnameResolver resolver;

    public KademliaRegularNode(RoutingNode routingNode, StorageNode storageNode, int tresholdResponsibility, UnameResolver resolver, UnionId selfUnid){
        this.storageNode = storageNode;
        this.routingNode = routingNode;
        this.tresholdResponsibility = tresholdResponsibility;
        this.resolver = resolver;
        this.selfUnionId = selfUnid;
    }


    /**
     *
     * Метод может вернуть
     * 1. Список владельцев контента
     * Или
     * 2. Список ближайших нод
     * Нужен какой-то флаг? ИЛИ NodeInfo это ключ, в Мапе, значение в которой контент , который есть у ноды?
     *
     * @param networkId
     * @return
     */
    @Override
    public List<NodeInfo> lookupContentStorages(NetworkId networkId) {
       //Todo невверно, т.к. в случае если нода Выложила контент, то
        //или проверять в списке выложенного контента ,а в потом ответсвенность
        //или вообще не проверять ответсвенность
        if (!checkResponsibility(networkId)){
            findClosestNode(networkId);
        }
        //можно ускорить проверку фильтром блума ложные Да, но всегда верные НЕТ
        //Если точно нет
        List<Content> content = storageNode.getContent(networkId);
        if (content.isEmpty()){
            findClosestNode(networkId);
        }
        return null;

    }

    public Set<NodeInfo> findClosestNode(NetworkId networkId) {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutDown() {

    }

    @Override
    public List<Content> getContent(NetworkId networkId) {
        return null;
    }

    @Override
    public NetworkId putContent(Content content) {
        return null;
    }

    @Override
    public List<Content> getContent(UnionId networkId) {
        return null;
    }

    @Override
    public int thresholdResponsibility() {
        return tresholdResponsibility;
    }

    @Override
    public boolean checkResponsibility(NetworkId networkId) {
        storageNode.checkResponsibility(networkId);

        //todo юзать верхний метод, а это в него перенести
        return Optional.ofNullable(networkId)
                .map(resolver::resolve)
                .map(resolver::resolve)
                .map(unionId -> unionId.computeDistance(unionId.asBytes()))
                .filter(this::lessThenThreshold)
                .isPresent();
    }

    private boolean lessThenThreshold(byte[] bytes){
        return true;
    }
}
