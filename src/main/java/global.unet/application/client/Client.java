package global.unet.application.client;

import global.unet.domain.messages.Message;

public interface Client {
    <T extends Message> void send(T message);
}
