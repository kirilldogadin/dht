package global.unet.domain.structures;

import global.unet.domain.id.UnionId;
import global.unet.domain.node.NodeType;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;

/**
 * Информация о ноде
 */
public class NodeInfo implements Serializable {

    private final URI uri;
    final UnionId unionId;
    final int port;
    final NodeType nodeType;
    
    // Время последней активности узла
    private Instant lastSeen;
    
    // Флаг, указывающий, активен ли узел
    private boolean active;
    
    // Счетчик неудачных попыток связи
    private int failedAttempts;

    public NodeInfo(URI uri, UnionId unionId, int port, NodeType nodeType) {
        this.uri = uri;
        this.unionId = unionId;
        this.port = port;
        this.nodeType = nodeType;
        this.lastSeen = Instant.now();
        this.active = true;
        this.failedAttempts = 0;
    }

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
    
    /**
     * Обновляет время последней активности узла
     */
    public void updateLastSeen() {
        this.lastSeen = Instant.now();
        this.active = true;
        this.failedAttempts = 0;
    }
    
    /**
     * Получает время последней активности узла
     * @return время последней активности
     */
    public Instant getLastSeen() {
        return lastSeen;
    }
    
    /**
     * Проверяет, активен ли узел
     * @return true, если узел активен
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Устанавливает статус активности узла
     * @param active новый статус активности
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Увеличивает счетчик неудачных попыток связи
     * @return новое значение счетчика
     */
    public int incrementFailedAttempts() {
        return ++failedAttempts;
    }
    
    /**
     * Получает количество неудачных попыток связи
     * @return количество неудачных попыток
     */
    public int getFailedAttempts() {
        return failedAttempts;
    }
    
    /**
     * Сбрасывает счетчик неудачных попыток связи
     */
    public void resetFailedAttempts() {
        this.failedAttempts = 0;
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

