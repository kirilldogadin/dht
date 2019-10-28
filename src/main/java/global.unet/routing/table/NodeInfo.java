package global.unet.routing.table;

import global.unet.id.NetworkId;
import global.unet.node.NodeType;

import java.net.URI;
import java.util.Objects;

/**
 * Информация о ноде
 */
//Todo Equals Hashcode зранятся в джава коллекции
public class NodeInfo {

    private final URI uri;
    final NetworkId networkId;
    final int port;
    //Todo должно ли быть здесь с точки зрения организация инкапусляции( находится в другом пакейдже)
    final NodeType nodeType;

    public NodeInfo(URI uri, NetworkId networkId, int port, NodeType nodeType) {
        this.uri = uri;
        this.networkId = networkId;
        this.port = port;
        this.nodeType = nodeType;
    }

    /**
     * create as default type REGULAR_NODE
     * @see NodeType#REGULAR_NODE
     * @param uri
     * @param networkId
     * @param port
     */
    public NodeInfo(URI uri, NetworkId networkId, int port) {
        this(uri, networkId, port, NodeType.REGULAR_NODE);
    }

    public URI getUri() {
        return uri;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInfo)) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return port == nodeInfo.port &&
                uri.equals(nodeInfo.uri) &&
                networkId.equals(nodeInfo.networkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, networkId, port);
    }
}