package global.unet.id;

import global.unet.structures.NodeInfo;

/**
 * информация о ноде на уровне сети Union
 */
public class UnionNodeInfo {

    public final UnionId networkId;
    public final NodeInfo source;
    public final UnionId nodeId;

    public UnionNodeInfo(UnionId nodeId, UnionId networkId, NodeInfo source) {
        this.networkId = networkId;
        this.source = source;
        this.nodeId = nodeId;
    }


}
