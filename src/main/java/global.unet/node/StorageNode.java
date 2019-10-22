package global.unet.node;

import global.unet.id.NetworkId;
import global.unet.id.UnionId;
import global.unet.storage.Content;
import global.unet.storage.Storage;

import java.util.List;

/**
 * Интерфейс ноды которая занимается хранением
 */
public interface StorageNode extends Node{
    List<Content> getContent(NetworkId networkId);
    NetworkId putContent(Content content);
    List<Content> getContent(UnionId networkId);
    int thresholdResponsibility();

    /**
     * Проверяет входит ли идентификатор в область ответсвтенности узла
     * @param networkId
     * @return true if
     */
    boolean checkResponsibility(NetworkId networkId);
}
