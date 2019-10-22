package global.unet.id;

/**
 * Unique Id of one Union
 */
//Todo параметры можно брать из конфига длина, метрика(xor или другая операция)
//Todo CustomId
public interface UnionId extends NetworkId {

    /**
     * return space of potential UnionId
     * space - is m bits of number (digit capacity)
     *
     * @return count bits of space (digit capacity)
     */
    int getSpaceOfUnionId();

    UnionId computeDistanceAsUnionId(UnionId in);

    byte[] computeDistance(UnionId in);

    //надо ли?
    //TODO мб вынести в отдельный класс и отдельно связать их?
    interface Metric {

    }

}
