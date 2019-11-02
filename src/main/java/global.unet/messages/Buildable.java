package global.unet.messages;

import java.util.function.Consumer;

public interface Buildable<T extends Message> {

    //TODO как вернуть билдер статичным методом не создавая объект сообщения
    BaseMessage.BaseBuilder<T> builder(Consumer<BaseMessage.BaseBuilder<T>> preBuilder);
}
