package global.unet.storage;

import java.util.List;

/**
 * описывает класс хранение контента в пространстве Space Union
 * отличие от StorageNode в том, что он содержит операции непосредственно с хранилищем, а не нодой
 * StorageNode has Storage
 */
public interface Storage<K,V> {

    List<V> getContent(K key);
    //todo подумать мб возвращать key, т.к. хеш может считаться только внутри
    void putContent(K key, List<V> value);
}
