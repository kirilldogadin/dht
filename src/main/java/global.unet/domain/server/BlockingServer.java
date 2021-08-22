package global.unet.domain.server;

import global.unet.domain.messages.Message;
import global.unet.domain.protocol.ping.PingMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingServer extends BaseServer {

    private byte[] buf = new byte[256];

    final ServerSocket serverSocket;

    public BlockingServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void start() {
        checkMessageHandler();
        try {
            run();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        isRunning = true;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    public void run() throws IOException, ClassNotFoundException {
        Socket clientSocket = null;
        while (isRunning) {
            clientSocket = serverSocket.accept();
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            Object object = in.readObject();
            System.out.println(object);
            //Todo здесь попытка сделать асинхронный ответ, но это блокирующий сервер, так что овтет надо сразу давать
            checkMessageHandler();
            messageHandler.accept((PingMessage) object);
        }
    }

    public Message deserialize(Object msg) {
        //todo проверяем на типы, тут зашит, но вообще список динамический List<Handler>
        return (PingMessage) msg;
    }
}
