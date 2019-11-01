package global.unet.server;

import global.unet.messages.Ping;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BlockingClient {

    public void sendMessage(Ping ping){
        Socket client = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            client = new Socket("localhost", 4445);

            out = new ObjectOutputStream(client.getOutputStream());
//            in = new ObjectInputStream(client.getInputStream());

            out.writeObject(ping);
            out.flush();

//close resources

            out.close();
//            in.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
