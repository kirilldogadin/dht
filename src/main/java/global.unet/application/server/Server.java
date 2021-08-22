package global.unet.application.server;

import global.unet.domain.messages.Message;

import java.util.function.Consumer;

public interface Server {

    /**
     *
     * @param messageHandler обработчик которму будет передано полученное сообщение
     */
    void setMessageHandler(Consumer<Message> messageHandler);
    void start();
    void stop();

}
