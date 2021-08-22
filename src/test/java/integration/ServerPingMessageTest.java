package integration;

import global.unet.application.client.RawSocketBlockingClient;
import global.unet.domain.notitifier.Notifier;
import global.unet.domain.receiver.MessageReceiver;
import global.unet.router.KadUnidRouter;
import global.unet.domain.id.UnionNodeInfo;
import global.unet.domain.id.UnionId;
import global.unet.domain.messages.*;
import global.unet.domain.node.KademliaRoutingNode;
import global.unet.application.server.*;
import global.unet.domain.structures.NodeInfo;
import global.unet.domain.protocol.ping.PingMessage;
import org.junit.Test;
import util.TestUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static java.lang.Thread.sleep;

public class ServerPingMessageTest extends TestUtil
{

    private final int SERVER_PORT = 4445;

    @Test
    public void test() throws URISyntaxException, InterruptedException
    {

        //nodeBaseInfo
        UnionId nodeId = generateUnid();
        UnionId networkId = generateUnid();
        NodeInfo selfNodeInfo = new NodeInfo(new URI("localhost"), nodeId, SERVER_PORT);
        UnionNodeInfo unionNodeInfo = new UnionNodeInfo(nodeId, networkId, selfNodeInfo);

        KadUnidRouter unidRouter = new KadUnidRouter(nodeId);

        Server server;
        MessageReceiver messageReceiver;

        server = new RawSocketBlockingServer(SERVER_PORT);
        //TODO server::sendMessage - это для асинхронного , вообще тут должно быть более верхнеуровневая конструкция
        // например какой-то Req2RespHandler
        // что=то типа Req2RespHandler(Server)

        //Todo создает НОВОГО клиента для ответа
        Notifier<Message> receiverNotifier = message -> new RawSocketBlockingClient("localhost", 4445).send(message);

        //TODO тут должен быть не server::send message а вообще что-то другое
        messageReceiver = new MessageReceiver(unidRouter, receiverNotifier, unionNodeInfo);
        server.setMessageHandler(messageReceiver::handle);

        KademliaRoutingNode kademliaRoutingNode = new KademliaRoutingNode(unidRouter, messageReceiver);
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
