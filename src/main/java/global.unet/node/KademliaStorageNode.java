package global.unet.node;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.storage.Content;
import global.unet.storage.Storage;

import java.util.List;

public class KademliaStorageNode implements StorageNode {

    private final Storage<NetworkId, Content> storage;

    public KademliaStorageNode(Storage storage) {
        this.storage = storage;
    }

    @Override
    public List<Content> getContent(NetworkId networkId) {
        return storage.getContent(networkId);
    }

    @Override
    public NetworkId putContent(Content content) {
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
    public boolean checkResponsibility(NetworkId networkId) {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutDown() {

    }
}
