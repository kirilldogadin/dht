package global.unet.domain.protocol;

import global.unet.domain.messages.Message;

/**
 * Handle input message(Request)
 */
public interface Handler<T extends Message> {
    void handle(Message message);
}
