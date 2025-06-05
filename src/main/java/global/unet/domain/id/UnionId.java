package global.unet.domain.id;

/**
 * Unique Id of one Union
 */
//Todo параметры можно брать из конфига длина, метрика(xor или другая операция)
//Todo CustomId
public interface UnionId extends BaseId {

    /**
     * return space of potential UnionId
     * space - is m bits of number (digit capacity)
     *
     * @return count bits of space (digit capacity)
     */
    int getUnidBitCount();

    UnionId computeDistanceAsUnionId(UnionId in);

    /**
     * вычислить дистанцию между аргументом и текущим объектом
     *
     * @param from unid для сравнения
     * @return distance between current and from
     */
    byte[] computeDistance(byte[] from);
    byte[] computeDistance(UnionId from);


    //TOdo вынести в метрику
    /**
     * вычислить дистанцию между двумя UnionId
     * @param from
     * @param to
     * @return
     */
    byte[] computeDistance(byte[] from, byte[] to);

    byte[] asBytes();

    //надо ли?
    //TODO мб вынести в отдельный класс и отдельно связать их?
    interface Metric<F,T,D> {
         D compute(F from,T to);
    }

}

