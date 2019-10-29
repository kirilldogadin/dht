package global.unet.id;

import global.unet.service.KadUnidRouter;
import global.unet.service.UnidRouter;
import global.unet.structures.NodeInfo;
import org.junit.Test;
import util.TestUtil;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class KadUnidRouterTest extends TestUtil {

    @Test
    public void UnidRouterTest(){

        UnidRouter kadUnidRouter = new KadUnidRouter(createRoutingTable());
        UnionId kademliaFixedId2 = createKademliaFixedId2();
        NodeInfo nodeInfo = nodeInfo1();
        kadUnidRouter.addNode(nodeInfo);
        Set<NodeInfo> closestNodes = kadUnidRouter.findClosestNodes(nodeInfo.getUnionId());
        assertTrue(closestNodes.contains(nodeInfo));
    }
}
