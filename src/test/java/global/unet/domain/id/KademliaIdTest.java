package global.unet.domain.id;


import org.junit.Test;
import util.UnionGenerator;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KademliaIdTest {

    private UnionGenerator unionGenerator;

    private void init() {
        unionGenerator = new UnionGenerator();
    }

    //Todo сделать все свойства проверить
    @Test
    public void xorMetricTest() {
        //with self
        init();

        UnionId kadId1 = unionGenerator.generateUnid();
        byte[] bytes = kadId1.computeDistance(kadId1);
        for (byte curByte : bytes) {
            assertEquals(0, curByte);
        }
        //symmetric
        UnionId kadId2 = unionGenerator.generateUnid();
        byte[] distanceFromFirstToSecond = kadId1.computeDistance(kadId2);
        byte[] distanceFromSecondToFirst = kadId2.computeDistance(kadId1);

        assertTrue(Arrays.equals(distanceFromFirstToSecond, distanceFromSecondToFirst));

    }


}
