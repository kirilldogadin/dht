package global.unet.domain.protocol.ping;

import global.unet.domain.messages.Message;
import global.unet.domain.notitifier.NotifierDrivenPort;
import global.unet.domain.protocol.MessageHandler;

import java.util.Optional;

/**
 *
 */
//TODO к сожалению пока лучше нет идеи имени
public class PingMessageHandler implements MessageHandler<PingMessageRequest> {

    final NotifierDrivenPort<Message> notifierDrivenPort;

    public PingMessageHandler(NotifierDrivenPort<Message> notifierDrivenPort) {
        this.notifierDrivenPort = notifierDrivenPort;
    }

    @Override
    public void handle(PingMessageRequest pingMessageRequest) {
        Optional.of(pingMessageRequest)
                .map(pingMsg -> new PingMessageResponse(
                        pingMsg.getDestination(),
                        pingMsg.getSource(),
                        pingMsg.getNetworkId(),
                        pingMsg.getMessageId()))
                .ifPresent(notifierDrivenPort::notify);
    }

}
