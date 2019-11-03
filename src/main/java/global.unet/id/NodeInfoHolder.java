package global.unet.id;

import global.unet.structures.NodeInfo;

public class NodeInfoHolder {

    public final UnionId networkId;
    public final NodeInfo source;
    public final UnionId nodeId;

    public NodeInfoHolder(UnionId nodeId, UnionId networkId, NodeInfo source) {
        this.networkId = networkId;
        this.source = source;
        this.nodeId = nodeId;
    }


}
