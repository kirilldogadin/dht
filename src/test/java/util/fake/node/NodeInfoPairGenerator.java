package util.fake.node;

import global.unet.domain.id.UnionId;
import global.unet.domain.structures.NodeInfo;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Генерирует пару Node на Localhost
 */
public class NodeInfoPairGenerator {

    private final UnionId unionId1;
    private final UnionId unionId2;
    private final URI LOCALHOST;
    private final int portOfNode1 = 228;
    private final int portOfNode2 = 229;

    {
        try {
            LOCALHOST = new URI("127.0.0.1");
        } catch (URISyntaxException e) {
            throw  new RuntimeException(e);
        }
    }

    public NodeInfoPairGenerator(UnionId unionId1, UnionId unionId2) {
       //TODO check 1 - разные объекты, различные UUID, общий NetworkId
        this.unionId1 = unionId1;
        this.unionId2 = unionId2;
    }

    /**
     * @return localhost unionId1 port1
     */
    public NodeInfo nodeInfo1(){
        return new NodeInfo(LOCALHOST, unionId1, portOfNode1);
    }
    /**
     * @return localhost unionId1 port2
     */
    public NodeInfo nodeInfo2(){
        return new NodeInfo(LOCALHOST, unionId2, portOfNode2);
    }
}
