package global.unet.messages.builders;

import global.unet.messages.Message;

/**
 * Генератор сообщений
 * @param <T>
 * @param <U>
 */
public interface MessageBuilderFabric<T extends Message, U extends BaseMessageBuilder<T>> {
    U builder();
}
