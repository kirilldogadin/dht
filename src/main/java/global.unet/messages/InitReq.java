package global.unet.messages;

import global.unet.structures.NodeInfo;

/**
 * Запрос на добавление себя
 * по идее это базовые сообщение которое содержит информацию о ноде отправителе
 */
public class InitReq implements Message {
    //Todo ID сообщения, ID СЕТИ - это Важно, чтобы понимать из какой сети пришло сообщение
    //TODO переделать на Base. То есть создать ещё один класс и от него расширить
    private final NodeInfo source;
    private final int hopes;
    private static final int HOPES_DEFAULT = 50;

    public InitReq(NodeInfo source, int hopes) {
        this.source = source;
        this.hopes = hopes;
    }

    public InitReq(NodeInfo source) {
        this(source, HOPES_DEFAULT);
    }

    public NodeInfo getSource() {
        return source;
    }

    public int getHopes() {
        return hopes;
    }
}
