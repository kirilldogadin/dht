package global.unet.domain.protocol;

import global.unet.domain.messages.Message;

/**
 * Handle input message(Request)
 */
public interface MessageHandler<T extends Message> {
    void handle(T message);
}
