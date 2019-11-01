package global.unet.service.receiver;

import global.unet.messages.*;
import global.unet.service.router.UnidRouter;
import global.unet.structures.NodeInfo;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Логика работы с принимаемыми сообщениями
 * <p>
 * UnionRouter в названниии означает, что только сообщения только с роутингом
 */
public class UnionRouterReceiver implements Receiver {

    //TOdo id и прочее


    //TOdo в будущем может иметь или несколько роутеров или несколько таблиц внутри роутера
    protected final UnidRouter unidRouter;
    protected final Consumer<Message> messageSender;
    protected final MessageBuilder messageBuilder;

    public UnionRouterReceiver(UnidRouter unidRouter, Consumer<Message> messageSender, MessageBuilder messageBuilder) {
        this.unidRouter = unidRouter;
        this.messageSender = messageSender;
        this.messageBuilder = messageBuilder;
    }

    public void handle(Message message) {

        //TODO идея , обработчик каждого типа это объект
        // Есть список этих обработчиков List<MessageReceiver>
        // При создании UnionRouterReceiver ( или динамически) добавляет список этих обработчиков
        // Каждый обработчик привязан к конкретному типу сообщения
        // (Можно динамически расширять протокол если добавлять в classpath объекты сообщения + сериализатор/десериализатор)
        // сделать вспомогательный класс который //аннотацию, которая говорит в каком пакейдже искать классы обработчиков

        //TODO метод по первым байтам подбирает тип сообщения,  (не десериализация, она в другом месте маппер или подобное)
        // потом пытается его построить и валидировать
        // Тип сообщения (или первый байт) ключом при парсинге, а значение обработчикомСообщения/шиной
        // Сообщение отправляется или в шину или нужному обработчику
        // На шину мб подписаны: NodeStat(статистика), NodeHolder(информация о живучести, время ласт активности
        // В статистика получает все сообщения для каждой ноды.

    }

    /**
     * @param closestIdReq
     * @return
     */
    public Set<NodeInfo> handle(ClosestIdReq closestIdReq) {
        unidRouter.addNode(closestIdReq.getSource());
        return unidRouter.findClosestNodes(closestIdReq.getResource());
    }

    public void handle(InitReq initReq) {
        unidRouter.addNode(initReq.getSource());

    }

    public void handle(FindContentHolders initReq) {
        messageBuilder.fillMessage(
                ClosestIdReq.builder()
                        .setResource(initReq.getResource()));

    }

    public void handle(UnionBootstrap unionBootstrap) {
        //Взять помимо ближайших из NodeHolder инормацию по всем ближайшим нодам, а также рейтинговым
        //послать сообщение
        messageSender.accept(null);
    }

    public void handle(Ping ping) {

        Pong pong = messageBuilder.fillMessage(
                Pong.builder()
                        .setDestination(ping.destination));

        messageSender.accept(pong);

    }

    public void handle(Pong pong) {

    }


}
