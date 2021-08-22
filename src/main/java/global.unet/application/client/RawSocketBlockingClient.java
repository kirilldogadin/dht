package global.unet.application.client;

import global.unet.domain.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Todo на уровень фреймворка
public class RawSocketBlockingClient implements Client{

    private final String host;
    private final int port;

    public RawSocketBlockingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public <T extends Message> void send(T message){
        Socket client = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            client = new Socket(host, port);
            out = new ObjectOutputStream(client.getOutputStream());
            out.writeObject(message);
            out.flush();
            out.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
