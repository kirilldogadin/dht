package global.unet.domain.node;

import global.unet.domain.config.NodeConfiguration;
import global.unet.domain.server.Server;
import global.unet.application.receiver.UnionRouterReceiver;
import global.unet.router.KadUnidRouter;
import global.unet.router.UnidRouter;

/**
 * Нода сервис которая выполняет только роутинг. Добавление нод
 *
 * Умеет делать роутинг GUNID тоже
 * Знает о такой сущность как конфиг
 */
public class KademliaRoutingNode implements RoutingNode {

    final Server server;
    final UnidRouter unidRouter;
    final UnionRouterReceiver unionRouterReceiver;

    // TODO конструктор с конфигом
    public KademliaRoutingNode(Server server, KadUnidRouter unidRouter, UnionRouterReceiver unionRouterReceiver) {
        this.server = server;
        this.unidRouter = unidRouter;
        this.unionRouterReceiver = unionRouterReceiver;
    }

    public KademliaRoutingNode(NodeConfiguration nodeConfiguration)
    {
        this(null, null, null);
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

}
