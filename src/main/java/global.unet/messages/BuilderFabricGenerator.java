package global.unet.messages;

import global.unet.id.NodeInfoHolder;

import java.lang.reflect.InvocationTargetException;

public class BuilderFabricGenerator {


    //Todo возвращает обобщенный
    public static <T extends Message,
            U extends BaseMessage.BaseBuilder<T>,
            D extends BaseMessage.MessageBuilderFabric<T, U>>
    BaseMessage.MessageBuilderFabric<T, U> createFabricBuilder(Class<U> builderClazz, NodeInfoHolder nodeInfoHolder) {
        return
                () -> {
                    try {
                        return builderClazz
                                .getConstructor(NodeInfoHolder.class)
                                .newInstance(nodeInfoHolder);
                    } catch (InstantiationException
                            | IllegalAccessException
                            | InvocationTargetException
                            | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                };
    }
}
