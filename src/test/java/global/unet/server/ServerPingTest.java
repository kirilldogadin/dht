package global.unet.server;

import global.unet.id.NodeInfoHolder;
import global.unet.id.UnionId;
import global.unet.messages.BaseMessage;
import global.unet.node.KademliaRoutingNode;
import global.unet.structures.NodeInfo;
import org.junit.Test;
import util.TestUtil;

import java.net.URI;
import java.net.URISyntaxException;

public class ServerPingTest extends TestUtil {

    @Test
    public void test() throws URISyntaxException {

        //nodeBaseInfo
        UnionId nodeId = generateUnid();
        UnionId networkId = generateUnid();
        NodeInfo selfNodeInfo = new NodeInfo(new URI("localhost"), nodeId, 4445);
        NodeInfoHolder nodeInfoHolder = new NodeInfoHolder(nodeId, networkId, selfNodeInfo);


        KademliaRoutingNode kademliaRoutingNode = new KademliaRoutingNode(nodeInfoHolder);
        Runnable serverStarting = kademliaRoutingNode::start;
        BlockingClient client = new BlockingClient();

        new Thread(serverStarting).start();

        BaseMessage.CommonFieldBuilder commonFieldBuilder = new BaseMessage.CommonFieldBuilder(nodeInfoHolder);
        Runnable sendingMessage = () -> {
    /*
            client.sendMessage(commonFieldBuilder
                    .fillMessageAsRequest(
                            //TODO создать фабрику билдера
                            Ping.builder()
                                    .setDestination(selfNodeInfo)

                    ));

    */
        };


    }
}
