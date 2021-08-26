package global.unet.domain.receiver;

import global.unet.application.client.RawSocketBlockingClient;
import global.unet.application.server.RawSocketBlockingServer;
import global.unet.application.server.Server;
import global.unet.domain.id.UnionId;
import global.unet.domain.id.UnionInfo;
import global.unet.domain.messages.BaseMessage;
import global.unet.domain.messages.Message;
import global.unet.domain.notitifier.Notifier;
import global.unet.domain.protocol.ping.PingMessage;
import global.unet.domain.structures.NodeInfo;
import global.unet.domain.router.KadUnidRouter;
import org.junit.Test;
import util.TestUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class MessageReceiverTest extends TestUtil
{

    private final int SERVER_PORT = 4445;

    @Test
    public void test() throws URISyntaxException, InterruptedException
    {

        //nodeBaseInfo
        UnionId nodeId = generateUnid();
        UnionId networkId = generateUnid();
        NodeInfo selfNodeInfo = new NodeInfo(new URI("localhost"), nodeId, SERVER_PORT);
        UnionInfo unionInfo = new UnionInfo(nodeId, networkId, selfNodeInfo);

        KadUnidRouter unidRouter = new KadUnidRouter(nodeId);

        Server server;
        MessageReceiver messageReceiver;

        //создает НОВОГО клиента для ответа
        Notifier<Message> receiverNotifier = message -> new RawSocketBlockingClient("localhost", 4445).send(message);

        //TODO тут должен быть не server::send message а вообще что-то другое
        messageReceiver = new MessageReceiver(unidRouter, receiverNotifier, unionInfo);
        server = new RawSocketBlockingServer(SERVER_PORT);
        server.setMessageHandler(messageReceiver::handle);

        Runnable serverStarting = server::start;

        new Thread(serverStarting).start();

//        serverStarting.run();  vbgf
        Runnable sendingMessage = () -> {
            receiverNotifier.notify(
                    new PingMessage(selfNodeInfo,
                            selfNodeInfo,
                            networkId,
                            UUID.randomUUID(),
                            BaseMessage.HOPES_DEFAULT)
            );
        };
        sendingMessage.run();
        //Todo не очень хорошее решение управлять потоками вручную

        sleep(1000);
//        new Thread(sendingMessage).start();

    }

}
