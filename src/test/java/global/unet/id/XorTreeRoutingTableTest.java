package global.unet.id;

import global.unet.routing.table.XorTreeRoutingTable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static util.TestUtil.createKademliaIdByTemplate;

public class XorTreeRoutingTableTest {

    @Test
    public void findBucketNumberTest() {

        //0000 0000 0111 1111
        KademliaId selfId = createKademliaIdByTemplate(1,(byte) 127, (byte) 0);

        //0000 0000 0010 0000
        KademliaId distanse = createKademliaIdByTemplate(1,(byte) 32, (byte) 0);

        XorTreeRoutingTable routingTable = new XorTreeRoutingTable(selfId, null);

        int bucketNumberForUnid = routingTable.getBucketNumberForUnid(distanse.asBytes());
        assertEquals(10,bucketNumberForUnid);
    }


    @Test
    public void findBucketTest() {

        //0000 0000 0010 0000
        KademliaId selfId = createKademliaIdByTemplate(1,(byte) 32, (byte) 0);
        //0000 0000 0111 1111
        KademliaId toId = createKademliaIdByTemplate(1,(byte) 127, (byte) 0);

        XorTreeRoutingTable routingTable = new XorTreeRoutingTable(selfId, null);
        routingTable.findBucket(toId);


    }

}
