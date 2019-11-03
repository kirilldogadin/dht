package global.unet.server;

import global.unet.id.UnionId;
import global.unet.messages.CommonFieldBuilder;
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

        CommonFieldBuilder commonFieldBuilder = new CommonFieldBuilder(nodeId, selfNodeInfo);
        Runnable sendingMessage = () -> {

            client.sendMessage(commonFieldBuilder
                    .fillMessageAsRequest(
                            //TODO создать фабрику билдера
                            Ping.builder()
                                    .setDestination(selfNodeInfo)

                    ));
        };


        client.sendMessage(commonFieldBuilder
                .fillMessageAsRequest(
                        Ping.builder()
                                .setDestination(selfNodeInfo)

                ));


    }
}
