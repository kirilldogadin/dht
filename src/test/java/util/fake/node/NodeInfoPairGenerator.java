package util.fake.node;

import global.unet.domain.id.UnionId;
import global.unet.domain.structures.NodeInfo;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Генерирует пару Node на Localhost
 */
public class NodeInfoPairGenerator {

    private final UnionId unionIdOfSourceNode;
    private final UnionId unionIdOfDestinationNode;
    private final URI LOCALHOST;
    private final int portOfSourceNode = 228;
    private final int portOfDestinationNode = 229;

    {
        try {
            LOCALHOST = new URI("127.0.0.1");
        } catch (URISyntaxException e) {
            throw  new RuntimeException(e);
        }
    }

    public NodeInfoPairGenerator(UnionId unionIdOfSourceNode, UnionId unionIdOfDestinationNode) {
       //TODO check 1 - разные объекты, различные UUID, общий NetworkId
        this.unionIdOfSourceNode = unionIdOfSourceNode;
        this.unionIdOfDestinationNode = unionIdOfDestinationNode;
    }

    /**
     * @return localhost unionId1 port1
     */
    public NodeInfo sourceNodeInfo(){
        return new NodeInfo(LOCALHOST, unionIdOfSourceNode, portOfSourceNode);
    }
    /**
     * @return localhost unionId1 port2
     */
    public NodeInfo destinationNodeInfo(){
        return new NodeInfo(LOCALHOST, unionIdOfDestinationNode, portOfDestinationNode);
    }
}
