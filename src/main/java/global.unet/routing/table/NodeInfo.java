package global.unet.routing.table;

import global.unet.id.NetworkId;
import global.unet.node.NodeType;

import java.net.InetAddress;

public class NodeInfo {

    private final InetAddress inetAddress;
    final NetworkId networkId;
    final int port;
    //Todo должно ли быть здесь с точки зрения организация инкапусляции( находится в другом пакейдже)
    final NodeType nodeType;

    public NodeInfo(InetAddress inetAddress, NetworkId networkId, int port, NodeType nodeType) {
        this.inetAddress = inetAddress;
        this.networkId = networkId;
        this.port = port;
        this.nodeType = nodeType;
    }

    public NodeInfo(InetAddress inetAddress, NetworkId networkId, int port) {
        this(inetAddress, networkId, port, NodeType.REGULAR_NODE);
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public NetworkId getNetworkId() {
        return networkId;
    }

    public int getPort() {
        return port;
    }

    public NodeType getNodeType() {
        return nodeType;
    }
}