package global.unet.domain.protocol.ping;

import global.unet.domain.messages.Message;
import global.unet.domain.notitifier.Notifier;
import global.unet.domain.protocol.MessageHandler;

import java.util.Optional;

/**
 *
 */
//TODO к сожалению пока лучше нет идеи имени
public class PingMessageHandler implements MessageHandler<PingMessage> {

    final Notifier<Message> notifier;

    public PingMessageHandler(Notifier<Message> notifier) {
        this.notifier = notifier;
    }

    @Override
    public void handle(PingMessage pingMessage) {
        Optional.of(pingMessage)
                .map(pingMsg -> new PingResponse(
                        pingMsg.getDestination(),
                        pingMsg.getSource(),
                        pingMsg.getNetworkId(),
                        pingMsg.getMessageId()))
                .ifPresent(notifier::notify);
    }

}
