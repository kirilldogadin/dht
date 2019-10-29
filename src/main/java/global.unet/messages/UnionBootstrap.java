package global.unet.messages;

import global.unet.id.GlobalUnionId;
import global.unet.structures.NodeInfo;

/**
 * Запрос на
 * Используется также для входа
 */
public class UnionBootstrap extends InitReq {

    private final GlobalUnionId targetUnion;

    public UnionBootstrap(NodeInfo source, GlobalUnionId targetUnion, int hopes) {
        super(source, hopes);
        this.targetUnion = targetUnion;
    }

    public UnionBootstrap(NodeInfo source, GlobalUnionId targetUnion) {
        super(source);
        this.targetUnion = targetUnion;
    }

    public GlobalUnionId getTargetUnion() {
        return targetUnion;
    }

}
