package global.unet.node;

import global.unet.config.NodeConfiguration;
import global.unet.id.NodeInfoHolder;
import global.unet.messages.BaseMessage;
import global.unet.server.BlockingServer;
import global.unet.server.Server;
import global.unet.service.receiver.UnionRouterReceiver;
import global.unet.service.router.KadUnidRouter;
import global.unet.service.router.UnidRouter;

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
    private final BaseMessage.CommonFieldBuilder commonFieldBuilder;

    // TODO конструктор с конфигом

    public KademliaRoutingNode(NodeInfoHolder nodeInfoHolder) {
        this.server = new BlockingServer();
        this.unidRouter = new KadUnidRouter(nodeInfoHolder.nodeId);
        //TODo подумать над конструкцией, мб цикличную зависимость можно разрешить
        this.commonFieldBuilder = new BaseMessage.CommonFieldBuilder(nodeInfoHolder);
        this.unionRouterReceiver = new UnionRouterReceiver(unidRouter, server::sendMessage, nodeInfoHolder);

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

}
