package global.unet.domain.node;

import global.unet.domain.config.NodeConfiguration;
import global.unet.domain.receiver.MessageReceiver;
import global.unet.router.KadUnidRouter;
import global.unet.router.UnidRouter;

/**
 * Нода сервис которая выполняет только роутинг. Добавление нод
 *
 * Умеет делать роутинг GUNID тоже
 * Знает о такой сущность как конфиг
 */
public class KademliaRoutingNode implements RoutingNode {

    final UnidRouter unidRouter;
    final MessageReceiver messageReceiver;

    // TODO избавиться от сервера в этой зависимости, здесть нижнеуровнвый зависит от верхнеуровнего
    public KademliaRoutingNode(KadUnidRouter unidRouter, MessageReceiver messageReceiver) {
        this.unidRouter = unidRouter;
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void start() {
        //TODO ничего не должен знать о сервере и о том КАК он работает, его должен стартатовать кто-то во внешнем слое
    }

    @Override
    public void shutDown() {
        //TODO ничего не должен знать о сервере и о том КАК он работает, его должен стартатовать кто-то во внешнем слое
        //пулы задач мягкое завершение потом остальное
    }

    public void initByConfig(NodeConfiguration nodeConfiguration){

    }


    //TODO вместо этих методов для работы с нодой через http/console также передавать как обработчик
    // server.setMessageHandler(unionRouterReceiver::handle); только здесь будет  console.setMessageHandler(unionRouterReceiver::handle)

}
