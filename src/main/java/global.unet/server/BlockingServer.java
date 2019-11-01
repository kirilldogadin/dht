package global.unet.server;

import global.unet.messages.Message;
import global.unet.messages.Ping;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingServer extends BaseServer {

    private byte[] buf = new byte[256];

    final ServerSocket serverSocket;


    public BlockingServer() {
        try {
            serverSocket = new ServerSocket(4445);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void start() {
        isRunning = true;
        try {
            run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
            messageHandler.accept(deserialize(object));


        }
    }

    public Message deserialize(Object msg) {
        //todo проверяем на типы, тут зашит, но вообще список динамический List<Handler>
        return (Ping) msg;

    }
}
