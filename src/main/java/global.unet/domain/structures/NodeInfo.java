package global.unet.domain.structures;

import global.unet.domain.id.UnionId;
import global.unet.domain.node.NodeType;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

/**
 * Информация о ноде
 */
//Todo Equals Hashcode зранятся в джава коллекции
    //Todo должен хранить не IP, а список IP (несколько сервисов могут работать под одной нодой (иметь одну зону ответсвенности)(реплики)
public class NodeInfo implements Serializable {

    private final URI uri;
    //Todo наверное nodeId , а не unionId?
    final UnionId unionId;
    final int port;
    //Todo должно ли быть здесь с точки зрения организация инкапусляции( находится в другом пакейдже)
    final NodeType nodeType;

    public NodeInfo(URI uri, UnionId unionId, int port, NodeType nodeType) {
        this.uri = uri;
        this.unionId = unionId;
        this.port = port;
        this.nodeType = nodeType;
    }
    //Todo валидатор конструтора

    /**
     * create as default type REGULAR_NODE
     * @see NodeType#REGULAR_NODE
     * @param uri
     * @param unionId
     * @param port
     */
    public NodeInfo(URI uri, UnionId unionId, int port) {
        this(uri, unionId, port, NodeType.REGULAR_NODE);
    }

    public URI getUri() {
        return uri;
    }

    public UnionId getUnionId() {
        return unionId;
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
                unionId.equals(nodeInfo.unionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, unionId, port);
    }
}