package global.unet.server;

import global.unet.messages.Message;

import java.util.function.Consumer;

public interface Server {

    /**
     *
     * @param messageHandler обработчик которму будет передано полученное сообщение
     */
    void setMessageHandler(Consumer<Message> messageHandler);
    void sendMessage(Message message);

    void start();
    void stop();

}
