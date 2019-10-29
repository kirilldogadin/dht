package global.unet.messages;

import global.unet.structures.NodeInfo;

public class FindContentHolders extends InitReq {

    public FindContentHolders(NodeInfo source, int hopes) {
        super(source, hopes);
    }

    public FindContentHolders(NodeInfo source) {
        super(source);
    }
}
