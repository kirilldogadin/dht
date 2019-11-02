package global.unet.server;

import global.unet.id.UnionId;
import global.unet.messages.OneUnionMessageFiller;
import global.unet.messages.Ping;
import global.unet.node.KademliaRoutingNode;
import global.unet.structures.NodeInfo;
import org.junit.Test;
import util.TestUtil;

import java.net.URI;
import java.net.URISyntaxException;

public class ServerPingTest extends TestUtil {

    @Test
    public void test() throws URISyntaxException {
        UnionId nodeId = generateUnid();
        UnionId networkId = generateUnid();
        NodeInfo selfNodeInfo = new NodeInfo(new URI("localhost"), nodeId, 4445);
        KademliaRoutingNode kademliaRoutingNode = new KademliaRoutingNode(nodeId, networkId, selfNodeInfo);
        Runnable serverStarting = kademliaRoutingNode::start;
        BlockingClient client = new BlockingClient();

        new Thread(serverStarting).start();

        OneUnionMessageFiller oneUnionMessageFiller = new OneUnionMessageFiller(nodeId, selfNodeInfo);
        Runnable sendingMessage = () -> {

            client.sendMessage(oneUnionMessageFiller
                    .createFullMessageRequest(
                            Ping.builder()
                                    .setDestination(selfNodeInfo)

                    ));
        };


        client.sendMessage(oneUnionMessageFiller
                .createFullMessageRequest(
                        Ping.builder()
                                .setDestination(selfNodeInfo)

                ));


    }
}
