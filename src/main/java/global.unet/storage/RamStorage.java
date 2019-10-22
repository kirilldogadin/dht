package global.unet.storage;

import global.unet.id.NetworkId;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe ram storage
 */
public class RamStorage<K extends NetworkId, V extends Content> implements Storage<K,V> {

    private final ConcurrentMap<K, List<V>> concurrentMap = new ConcurrentHashMap<>();

    @Override
    public List<V> getContent(K networkId) {
        return concurrentMap.get(networkId);
    }

    @Override
    public void putContent(K key, List<V> content) {
        concurrentMap.put(key,content);
    }

}
