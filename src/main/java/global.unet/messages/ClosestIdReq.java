package global.unet.messages;

import global.unet.id.UnionId;
import global.unet.structures.NodeInfo;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Запрос на поиск ближайшнего к Id ноды
 */
public class ClosestIdReq extends BaseMessageWithResource {

    private ClosestIdReq(NodeInfo source, NodeInfo destination, UnionId networkId, UUID messageId, int hopes, UnionId resource) {
        super(source, destination, networkId, messageId, hopes, resource);
    }


    //TODO подумать над названием. Мб это дто?
    public static BaseMessageWithResource.Builder<ClosestIdReq> builder(Consumer<BaseBuilder<ClosestIdReq>> preBuilder) {
        return new Builder(preBuilder);
    }

    private static class Builder extends BaseMessageWithResource.Builder<ClosestIdReq> {

        Builder(Consumer<BaseBuilder<ClosestIdReq>> preBuilder) {
            super(preBuilder);
        }

        @Override
        ClosestIdReq finalBuild() {
            return new ClosestIdReq(
                    source,
                    destination,
                    networkId,
                    messageId,
                    hopes,
                    resource);
        }


    }


}
