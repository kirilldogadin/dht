package global.unet.domain.receiver;

import global.unet.domain.notitifier.Notifier;
import global.unet.router.UnidRouter;
import global.unet.domain.id.UnionNodeInfo;
import global.unet.domain.messages.*;
import global.unet.domain.protocol.find.ClosestIdRequest;
import global.unet.domain.protocol.ping.PingMessage;
import global.unet.domain.protocol.ping.PingHandler;
import global.unet.domain.protocol.ping.PingResponse;

import java.util.Optional;
import java.util.Set;

import static java.util.function.Predicate.not;

/**
 * Логика работы с принимаемыми сообщениями
 * <p>
 * UnionRouter в названниии означает, что только сообщения только с роутингом без хранения
 * TODO набор операций формируется наобором поддерживающих соообщений
 * поэтому разделение роутеров как классов не акктуально
 */
public class MessageReceiver implements Receiver {

    //TOdo id и прочее
    //TOdo в будущем может иметь или несколько роутеров или несколько таблиц внутри роутера
    protected final UnidRouter unidRouter;
    protected final Notifier<Message> messageNotifier;
    protected final UnionNodeInfo unionNodeInfo;

    //TODO для всех типов все фарбрики? мб список фабрик

    public MessageReceiver(UnidRouter unidRouter, Notifier<Message> messageNotifier, UnionNodeInfo unionNodeInfo) {

        //TODO здесь создавать или он ещё в других местах мб?
        this.unidRouter = unidRouter;
        this.messageNotifier = messageNotifier;
        this.unionNodeInfo = unionNodeInfo;
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

        //TODO ДЕЛАЙ ЗАЕБИСЬ, А НЕ ЗАЕБИСЬ НЕ ДЕЛАЙ
        handle((PingMessage) message);

    }

    /**
     * @param closestIdRequest
     * @return
     */
    //TODO написать тест
    public void handle(ClosestIdRequest closestIdRequest) {

        //Вынести в метод. Скорее всего это делает Нода статистики, или NodeHolder который подписан на ноду статистики
        unidRouter.addNode(closestIdRequest.getSource());

        Optional.of(closestIdRequest)
                .map(BaseMessageWithResource::getResource)
                .map(unidRouter::findClosestNodes)
                .filter(not(Set::isEmpty))
                .ifPresentOrElse(nodeInfos ->
                                Optional.of(new ResourceResponse(
                                        unionNodeInfo.nodeInfo,
                                        closestIdRequest.getSource(),
                                        closestIdRequest.getNetworkId(),
                                        closestIdRequest.getMessageId(),
                                        closestIdRequest.getResource(),
                                        nodeInfos))
                                        .ifPresent(messageNotifier::notify),
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
        messageNotifier.notify(null);
    }

    public void handle(PingMessage pingMessage) {
        //TODO поменять на Ping operation
//        new PingHandler(pingMessage).handle(pingMessage);
        System.out.println(pingMessage);
        Optional.of(pingMessage)
                .map(pingMsg -> new PingResponse(
                        unionNodeInfo.nodeInfo,
                        pingMsg.getSource(),
                        pingMsg.getNetworkId(),
                        pingMsg.getMessageId()))
                .ifPresent(messageNotifier::notify);
    }

    public void handle(PingResponse pingResponse) {
        System.out.println(pingResponse);
    }


}
