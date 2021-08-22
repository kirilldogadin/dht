package global.unet.domain.id;


import org.junit.*;
import util.*;

import java.util.*;

import static org.junit.Assert.*;
import static util.TestUtil.generateUnid;

public class KademliaIdTest {

    //Todo сделать все свойства проверить
    @Test
    public void xorMetricTest() {
        //with self
        UnionId kadId1 = TestUtil.generateUnid();
        byte[] bytes = kadId1.computeDistance(kadId1);
        for (byte curByte : bytes) {
            assertEquals(0, curByte);
        }
        //symmetric
        UnionId kadId2 = TestUtil.generateUnid();
        byte[] distanceFromFirstToSecond = kadId1.computeDistance(kadId2);
        byte[] distanceFromSecondToFirst = kadId2.computeDistance(kadId1);

        assertTrue(Arrays.equals(distanceFromFirstToSecond, distanceFromSecondToFirst));

    }
}
