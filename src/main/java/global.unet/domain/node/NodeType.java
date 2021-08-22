package global.unet.domain.node;

/**
 * 
 * Для каждого типа рекоммендуется своя очередь оработки 
 * Приоритет
 * type тип пира. 
 * peer - стандартный участник роутер + сторадж
 * сторадж - 
 * виндов - 
 * очередь,  -
 * серчер(роутер), -
 * rating-node, 
 * авторитет
 */

public enum NodeType {
    
    REGULAR_NODE(1), //router + storage
    STORAGE(2),
    QUEUE(3),
    ROUTER(4),
    RATING(5),
    AUTHORITY(6),
    UNDEFINED(15);

    public final int codeType;

    NodeType( int codeType) {
        this.codeType = codeType;
    }
}
