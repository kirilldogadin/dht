package global.unet.domain.id;

import global.unet.domain.structures.NodeInfo;

/**
 * информация о ноде на уровне сети Union
 */
public class UnionNodeInfo {

    public final UnionId networkId;
    public final NodeInfo nodeInfo;
    public final UnionId nodeId;

    public UnionNodeInfo(UnionId nodeId, UnionId networkId, NodeInfo nodeInfo) {
        this.networkId = networkId;
        this.nodeInfo = nodeInfo;
        this.nodeId = nodeId;
    }


}
