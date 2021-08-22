package global.unet.domain.protocol.ping;

import global.unet.domain.messages.Message;
import global.unet.domain.protocol.Handler;

/**
 *
 */
//TODO к сожалению пока лучше нет идеи имени
public class PingHandler implements Handler<PingMessage> {

    final PingMessage pingMessage;

    public PingHandler(PingMessage pingMessage) {
        this.pingMessage = pingMessage;
    }

    @Override
    public void handle(Message message) {

    }

}
