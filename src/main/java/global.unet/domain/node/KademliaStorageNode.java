package global.unet.domain.node;

import global.unet.domain.id.BaseId;
import global.unet.domain.id.UnionId;
import global.unet.domain.storage.Content;
import global.unet.domain.storage.Storage;

import java.util.List;

public class KademliaStorageNode implements StorageNode {

    private final Storage<BaseId, Content> storage;

    public KademliaStorageNode(Storage storage) {
        this.storage = storage;
    }

    @Override
    public List<Content> getContent(BaseId baseId) {
        return storage.getContent(baseId);
    }

    @Override
    public BaseId putContent(Content content) {
        return null;
    }

    @Override
    public List<Content> getContent(UnionId networkId) {
        return null;
    }

    @Override
    public int thresholdResponsibility() {
        return 0;
    }

    @Override
    public boolean checkResponsibility(BaseId baseId) {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutDown() {

    }
}
