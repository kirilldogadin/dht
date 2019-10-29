package global.unet.node;

import global.unet.config.NodeConfiguration;
import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.server.Server;
import global.unet.server.WebSocketServer;
import global.unet.service.KadUnidRouter;
import global.unet.service.Receiver.UnionRouterReceiver;
import global.unet.service.UnidRouter;
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

    // TODO конструктор с конфигом

    public KademliaRoutingNode(UnionId unionId) {
        this.server = new WebSocketServer();
        this.unidRouter = new KadUnidRouter(unionId);
        //TODo подумать над конструкцией, мб цикличную зависимость можно разрешить
        unionRouterReceiver = new UnionRouterReceiver(unidRouter, server::sendMessage);
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
    // server.setMessageHandler(unionRouterReceiver::handle);

//    @Override
    public Set<NodeInfo> findClosestNode(NetworkId networkId) {
        throw new RuntimeException("method not implement yet");
    }

//    @Override
    public void addNode(NodeInfo nodeInfo) {
        unidRouter.addNode(nodeInfo);
    }
}
