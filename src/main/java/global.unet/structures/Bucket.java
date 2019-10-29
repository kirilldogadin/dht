package global.unet.structures;

import java.util.HashSet;
import java.util.Set;

/**
 * содержит список UNID
 * k-bucket
 *
 */
//TODO синхронизировать
    //Todo подумать над моделью многоппоточности у каждого потока своя таблица или синхронизировать доступ
    //TODO сделать вложенным в BucketFabric? а бакет сделать интерфейсом?
public class Bucket {
    // TODO ещё маску первые совпадающие биты)

    private final Set<NodeInfo> nodeInfoSet;
    private final int maxCapacity;
    private final int kResponsibility;

    public Bucket(int maxCapacity, int kResponsibility) {
        this.maxCapacity = maxCapacity;
        nodeInfoSet = new HashSet<>(maxCapacity);
        this.kResponsibility = kResponsibility;
    }

    Set<NodeInfo> getNodeInfoSet() {
        return nodeInfoSet;
    }

    void add(NodeInfo nodeInfo) {
        checkCapacity();
        nodeInfoSet.add(nodeInfo);
    }

    /**
     * Алгоритм вытеснения/пинга и тд для того, чтобы выяснить кого оставить
     * алогритм "доверяй соседу"
     */
    private void checkCapacity() {
        if (nodeInfoSet.size() >= maxCapacity) {
        }
    }

    /**
     *
     * @return responsibility first bits count of unid
     */
    public int getKResponsibility() {
        return kResponsibility;
    }
}
