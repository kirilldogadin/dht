package global.unet.server;

import global.unet.id.UnionNodeInfo;
import global.unet.id.UnionId;
import global.unet.messages.*;
import global.unet.node.KademliaRoutingNode;
import global.unet.structures.NodeInfo;
import org.junit.Test;
import util.TestUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;

public class ServerPingTest extends TestUtil
{

    @Test
    public void test() throws URISyntaxException
    {

        //nodeBaseInfo
        UnionId nodeId = generateUnid();
        UnionId networkId = generateUnid();
        NodeInfo selfNodeInfo = new NodeInfo(new URI("localhost"), nodeId, 4445);
        UnionNodeInfo unionNodeInfo = new UnionNodeInfo(nodeId, networkId, selfNodeInfo);


        Server server = new BlockingServer();
        KademliaRoutingNode kademliaRoutingNode = new KademliaRoutingNode(server, unionNodeInfo);
        Runnable serverStarting = kademliaRoutingNode::start;
        BlockingClient client = new BlockingClient();

        ExecutorService executorService = Executors.newCachedThreadPool();

        new Thread(serverStarting).start();

//        serverStarting.run();
        Runnable sendingMessage = () -> {
            client.sendMessage(
                    new Ping(selfNodeInfo,
                            selfNodeInfo,
                            networkId,
                            UUID.randomUUID(),
                            BaseMessage.HOPES_DEFAULT)
            );
        };
        sendingMessage.run();
//        new Thread(sendingMessage).start();

    }
}
