package global.unet.domain.structures;

import global.unet.domain.id.UnionId;
import global.unet.domain.node.NodeType;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

/**
 * Тестовая версия класса NodeInfo для использования в тестах
 * Упрощенная версия без зависимости от URI
 */
public class NodeInfo implements Serializable {

    private final String name;
    final UnionId unionId;
    final int port;
    final NodeType nodeType;

    public NodeInfo(UnionId unionId, String name, int port, NodeType nodeType) {
        this.name = name;
        this.unionId = unionId;
        this.port = port;
        this.nodeType = nodeType;
    }

    /**
     * create as default type REGULAR_NODE
     * @see NodeType#REGULAR_NODE
     * @param unionId
     * @param name
     * @param port
     */
    public NodeInfo(UnionId unionId, String name, int port) {
        this(unionId, name, port, NodeType.REGULAR_NODE);
    }

    public String getName() {
        return name;
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
                name.equals(nodeInfo.name) &&
                unionId.equals(nodeInfo.unionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unionId, port);
    }
}

