package global.unet.server;

import global.unet.id.UnionNodeInfo;
import global.unet.id.UnionId;
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
        UnionNodeInfo unionNodeInfo = new UnionNodeInfo(nodeId, networkId, selfNodeInfo);


        KademliaRoutingNode kademliaRoutingNode = new KademliaRoutingNode(unionNodeInfo);
        Runnable serverStarting = kademliaRoutingNode::start;
        BlockingClient client = new BlockingClient();

        new Thread(serverStarting).start();

//        BaseMessage.CommonFieldBuilder<Ping> commonFieldBuilder = new BaseMessage.CommonFieldBuilder<>(nodeInfoHolder);
//        BaseMessage.BaseMessageBuilder commonFieldBuilder2 = Ping.builder();
        Runnable sendingMessage = () -> {

//            client.sendMessage(
//                            //TODO создать фабрику билдера
//                            Ping.builder( ))
//                            Pong.MessageBuilder( ))
//                                    .setDestination(selfNodeInfo)
//
//                    ));


        };


    }
}
