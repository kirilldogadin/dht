package global.unet.domain.node;

import global.unet.domain.id.BaseId;
import global.unet.domain.id.UnionId;
import global.unet.domain.storage.Content;

import java.util.List;

/**
 * Интерфейс ноды которая занимается хранением
 */
public interface StorageNode extends Node{
    List<Content> getContent(BaseId baseId);
    BaseId putContent(Content content);
    List<Content> getContent(UnionId networkId);
    int thresholdResponsibility();

    /**
     * Проверяет входит ли идентификатор в область ответсвтенности узла
     * @param baseId
     * @return true if
     */
    boolean checkResponsibility(BaseId baseId);
}
