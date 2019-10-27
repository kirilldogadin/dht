package global.unet.id;


import org.junit.Test;
import util.TestUtil;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
