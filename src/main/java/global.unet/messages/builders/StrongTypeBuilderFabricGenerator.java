package global.unet.messages.builders;

import global.unet.messages.Message;

import java.util.function.Supplier;

/**
 * Класс-генератор фабрик Билдеров.
 * То есть генератор генераторов билдеров.
 * то есть генератор генераторов временных (нефинальных) сообщений.
 */
public class StrongTypeBuilderFabricGenerator {

    //Todo возвращает обобщенный
    public static <T extends Message,
            U extends BaseMessageBuilder<T>>

    MessageBuilderFabric<T, U> fabric(Supplier<U> messageBuilderSupplier) {
        return () -> messageBuilderSupplier.get();
    }
}
