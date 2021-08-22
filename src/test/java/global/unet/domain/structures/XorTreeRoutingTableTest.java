package global.unet.domain.structures;

import global.unet.domain.id.*;
import org.junit.*;

import java.net.*;
import java.util.*;

import static org.junit.Assert.*;
import static util.TestUtil.*;

public class XorTreeRoutingTableTest {

    //TODO тесты на конструктор
    @Test
    public void addNodeTest() throws URISyntaxException {
        //0000 0000 0010 0000
        KademliaId selfId = createKademliaIdByTemplate(1,(byte) 32, (byte) 0);
        //0000 0000 0111 1111
        KademliaId toId = createKademliaIdByTemplate(1,(byte) 127, (byte) 0);

        XorTreeRoutingTable routingTable = new XorTreeRoutingTable(selfId);

        NodeInfo nodeInfo1 = new NodeInfo(new URI("0.0.0.0"),toId,228);

        routingTable.addNode(nodeInfo1);
        Set<NodeInfo> closestUnionIds = routingTable.findClosestNodes(toId);
        assertTrue(closestUnionIds.contains(nodeInfo1));
    }


    @Test
    public void findBucketNumberTest() {

        //0000 0000 0111 1111
        KademliaId selfId = createKademliaIdByTemplate(1,(byte) 127, (byte) 0);

        //0000 0000 0010 0000
        KademliaId distanse = createKademliaIdByTemplate(1,(byte) 32, (byte) 0);

        XorTreeRoutingTable routingTable = new XorTreeRoutingTable(selfId);

        int bucketNumberForUnid = routingTable.getBucketNumberForUnid(distanse.asBytes());
        assertEquals(10,bucketNumberForUnid);
    }

    //TODO удалить или изменить
    @Test
    public void findBucketTest() {

        //0000 0000 0010 0000
        KademliaId selfId = createKademliaIdByTemplate(1,(byte) 32, (byte) 0);
        //0000 0000 0111 1111
        KademliaId toId = createKademliaIdByTemplate(1,(byte) 127, (byte) 0);

        XorTreeRoutingTable routingTable = new XorTreeRoutingTable(selfId);

        Bucket bucket = routingTable.findBucket(toId);
        assertTrue(bucket.getKResponsibility() == 9);

    }






}
