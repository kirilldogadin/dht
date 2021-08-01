package global.unet.messages.builders;

import global.unet.id.UnionNodeInfo;
import global.unet.messages.Message;

import java.lang.reflect.InvocationTargetException;

public class ReflectionBuilderFabricGenerator {


    //Todo возвращает обобщенный
    public static <T extends Message,
            U extends BaseMessageBuilder<T>,
            D extends MessageBuilderFabric<T, U>>
    MessageBuilderFabric<T, U> fabric(Class<U> builderClazz, UnionNodeInfo unionNodeInfo) {
        return
                () -> {
                    try {
                        return builderClazz
                                .getConstructor(UnionNodeInfo.class)
                                .newInstance(unionNodeInfo);
                    } catch (InstantiationException
                            | IllegalAccessException
                            | InvocationTargetException
                            | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                };
    }
}
