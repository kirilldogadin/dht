package global.unet.domain.config;

import lombok.Builder;

/**
 * Содержит базовые настройки такие как:
 *
 * разрядность ключа - дефолт 160 бит
 * тип метрика - дефолт Xor
 * емкость к-бакета
 */
@Builder
public class NodeConfiguration {

    private final static int MAX_CAPACITY_DEFAULT = 20;

    private Integer capacity;

    /**
     * Capacity (K) of k-bucket
     * @return
     */
    public Integer getCapacity(){
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
