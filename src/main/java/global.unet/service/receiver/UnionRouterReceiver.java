package global.unet.service.receiver;

import global.unet.messages.*;
import global.unet.service.router.UnidRouter;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.function.Predicate.not;

/**
 * Логика работы с принимаемыми сообщениями
 * <p>
 * UnionRouter в названниии означает, что только сообщения только с роутингом без хранения
 * TODO набор операций формируется наобором поддерживающих соообщений
 * поэтому разделение роутеров как классов не акктуально
 */
public class UnionRouterReceiver implements Receiver {

    //TOdo id и прочее
    //TOdo в будущем может иметь или несколько роутеров или несколько таблиц внутри роутера
    protected final UnidRouter unidRouter;
    protected final Consumer<Message> messageSender;
    protected final MessagePreBuilder messagePreBuilder;

    //TODO для всех типов все фарбрики? мб список фабрик
    private final MessageBuilderFabrice<Pong> pongBuilderFabrice;

    public UnionRouterReceiver(UnidRouter unidRouter, Consumer<Message> messageSender, MessagePreBuilder messagePreBuilder) {
        this.unidRouter = unidRouter;
        this.messageSender = messageSender;
        this.messagePreBuilder = messagePreBuilder;
        pongBuilderFabrice = new PongBuilderFabrice(messagePreBuilder);
    }

    public void handle(Message message) {

        //TODO пока что сделать один простой класс , чтобы протестировать вместе с сервером всю структуру

        //TODO идея , обработчик каждого типа это объект
        // Есть список этих обработчиков List<MessageReceiver>
        // При создании UnionRouterReceiver ( или динамически) добавляет список этих обработчиков
        // Каждый обработчик привязан к конкретному типу сообщения и содержит валидатор
        // (Можно динамически расширять протокол если добавлять в classpath объекты сообщения + сериализатор/десериализатор)
        // сделать вспомогательный класс который //аннотацию, которая говорит в каком пакейдже искать классы обработчиков

        //TODO метод по первым байтам подбирает тип сообщения,  (не десериализация, она в другом месте маппер или подобное)
        // потом пытается его построить и валидировать
        // (обработчик сообщения именно сам содержит логику валидации)(валидаторы формата. Можно валидировать уже конкретное сообщение
        // Тип сообщения (или первый байт) ключом при парсинге, а значение обработчикомСообщения/шиной
        // Сообщение отправляется или в шину или нужному обработчику
        // На шину мб подписаны: NodeStat(статистика), NodeHolder(информация о живучести, время ласт активности
        // статистика получает все сообщения для каждой ноды
        // также сделать log

        //TODO
        handle((Ping) message);

    }

    /**
     * @param closestIdReq
     * @return
     */
    public void handle(ClosestIdReq closestIdReq) {

        //Вынести в метод. Скорее всего это делает Нода статистики, или NodeHolder который подписан на ноду статистики
        unidRouter.addNode(closestIdReq.getSource());

        Optional.of(closestIdReq)
                .map(BaseMessageWithResource::getResource)
                .map(unidRouter::findClosestNodes)
                .filter(not(Set::isEmpty))
                .ifPresentOrElse(nodeInfos ->
                                Optional.of(ResourceResponse.builder(messagePreBuilder::fillMessageAsResponse)
                                        .setNodeInfos(nodeInfos)
                                        .setMessageId(closestIdReq.getMessageId())
                                        .setDestination(closestIdReq.getSource())
                                        .build())
                                        .ifPresent(messageSender),

                        () -> { // если ближайшие не найдены
                            //подумать может ли такое быть и если да, то как обрабатывать
                        });

    }

    public void handle(InitReq initReq) {
        unidRouter.addNode(initReq.getSource());

    }

    public void handle(FindContentHolders initReq) {

    }

    public void handle(UnionBootstrap unionBootstrap) {
        //Взять помимо ближайших из NodeHolder инормацию по всем ближайшим нодам, а также рейтинговым
        //послать сообщение
        messageSender.accept(null);
    }

    public void handle(Ping ping) {
        System.out.println(ping);
        Optional.of(ping)
                .map(pingMsg -> pongBuilderFabrice.builder()
                        .setDestination(pingMsg.getSource())
                        .setMessageId(pingMsg.getMessageId())
                        .build())
                .ifPresent(messageSender);


    }

    public void handle(Pong pong) {


    }


}
