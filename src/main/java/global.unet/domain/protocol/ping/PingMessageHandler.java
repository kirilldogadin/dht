package global.unet.domain.protocol.ping;

import global.unet.domain.messages.Message;
import global.unet.domain.notitifier.NotifierDrivenPort;
import global.unet.domain.protocol.MessageHandler;

import java.util.Optional;

/**
 *
 */
//TODO к сожалению пока лучше нет идеи имени
public class PingMessageHandler implements MessageHandler<PingMessage> {

    final NotifierDrivenPort<Message> notifierDrivenPort;

    public PingMessageHandler(NotifierDrivenPort<Message> notifierDrivenPort) {
        this.notifierDrivenPort = notifierDrivenPort;
    }

    @Override
    public void handle(PingMessage pingMessage) {
        Optional.of(pingMessage)
                .map(pingMsg -> new PingResponse(
                        pingMsg.getDestination(),
                        pingMsg.getSource(),
                        pingMsg.getNetworkId(),
                        pingMsg.getMessageId()))
                .ifPresent(notifierDrivenPort::notify);
    }

}
