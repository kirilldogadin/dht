package global.unet.service.router;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;
import org.junit.Test;
import util.TestUtil;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class KadUnidRouterTest extends TestUtil {

    @Test
    public void UnidRouterTest(){

        UnidRouter kadUnidRouter = new KadUnidRouter(constantId());
        UnionId unionId2 = constantId2();
        NodeInfo nodeInfo = nodeInfo1();
        kadUnidRouter.addNode(nodeInfo);
        Set<NodeInfo> closestNodes = kadUnidRouter.findClosestNodes(nodeInfo.getUnionId());
        assertTrue(closestNodes.contains(nodeInfo));
    }
}
