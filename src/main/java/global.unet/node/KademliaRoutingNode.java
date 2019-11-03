package global.unet.node;

import global.unet.config.NodeConfiguration;
import global.unet.id.BaseId;
import global.unet.id.UnionId;
import global.unet.messages.CommonFieldBuilder;
import global.unet.server.BlockingServer;
import global.unet.server.Server;
import global.unet.service.receiver.UnionRouterReceiver;
import global.unet.service.router.KadUnidRouter;
import global.unet.service.router.UnidRouter;
import global.unet.structures.NodeInfo;

import java.util.Set;

/**
 * Нода сервис которая выполняет только роутинг. Добавление нод
 *
 * Умеет делать роутинг GUNID тоже
 * Знает о такой сущность как конфиг
 */
public class KademliaRoutingNode implements RoutingNode {

    final Server server;
    //todo должен быть сервис, а не сразу стукртукра
    final UnidRouter unidRouter;
    final UnionRouterReceiver unionRouterReceiver;
    private final CommonFieldBuilder commonFieldBuilder;

    // TODO конструктор с конфигом

    public KademliaRoutingNode(UnionId nodeId, UnionId networkId, NodeInfo selfNodeInfo) {
        this.server = new BlockingServer();
        this.unidRouter = new KadUnidRouter(nodeId);
        //TODo подумать над конструкцией, мб цикличную зависимость можно разрешить
        this.commonFieldBuilder = new CommonFieldBuilder(networkId, selfNodeInfo);
        this.unionRouterReceiver = new UnionRouterReceiver(unidRouter, server::sendMessage, commonFieldBuilder);

        //TOdo вот тут сделать через билдер, чтобы пока мы не вызовем build сам сервер не создавался
        server.setMessageHandler(unionRouterReceiver::handle);
    }

    @Override
    public void start() {

        server.start();
    }

    @Override
    public void shutDown() {
        //пулы задач мягкое завершение потом остальное
    }

    public void initByConfig(NodeConfiguration nodeConfiguration){

    }


    //TODO вместо этих методов для работы с нодой через http/console также передавать как обработчик
    // server.setMessageHandler(unionRouterReceiver::handle); только здесь будет  console.setMessageHandler(unionRouterReceiver::handle)

    public Set<NodeInfo> findClosestNode(BaseId baseId) {
        throw new RuntimeException("method not implement yet");
    }

    public void addNode(NodeInfo nodeInfo) {
        unidRouter.addNode(nodeInfo);
    }
}
