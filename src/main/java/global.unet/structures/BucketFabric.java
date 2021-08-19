package global.unet.structures;

import java.util.function.IntSupplier;

/**
 * Содержит значения из конфига или дефолтные
 * maxCapacity - максимальный размер к-бакета
 */
public class BucketFabric {

    public final static int MAX_CAPACITY_DEFAULT = 20;
    private final int maxCapacity;

    //TODO в вызывающем коде не используется все равно передается просто значение
    public BucketFabric(IntSupplier capacitySupplier) {
        int capacity = capacitySupplier.getAsInt();
        validateCapacity(capacity);
        this.maxCapacity = capacity;
    }

    public Bucket createBucket(int kResponsibility) {
        return new Bucket(maxCapacity, kResponsibility);
    }

    private void validateCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("maxCapacity must be positive. " +
                    "Recommended value " + MAX_CAPACITY_DEFAULT);
        }
    }

}
