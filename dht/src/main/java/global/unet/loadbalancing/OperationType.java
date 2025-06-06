package global.unet.loadbalancing;

/**
 * Типы операций для балансировки нагрузки
 */
public enum OperationType {
    
    /**
     * Операция поиска значения
     */
    FIND_VALUE("FIND_VALUE", 1.0, true),
    
    /**
     * Операция сохранения данных
     */
    STORE("STORE", 2.0, false),
    
    /**
     * Операция поиска узлов
     */
    FIND_NODE("FIND_NODE", 1.5, true),
    
    /**
     * Операция ping
     */
    PING("PING", 0.5, true),
    
    /**
     * Операция репликации
     */
    REPLICATE("REPLICATE", 2.5, false),
    
    /**
     * Операция восстановления
     */
    RECOVER("RECOVER", 3.0, false);
    
    private final String name;
    private final double weight; // Вес операции для расчета нагрузки
    private final boolean cacheable; // Можно ли кэшировать результат
    
    OperationType(String name, double weight, boolean cacheable) {
        this.name = name;
        this.weight = weight;
        this.cacheable = cacheable;
    }
    
    public String getName() { return name; }
    public double getWeight() { return weight; }
    public boolean isCacheable() { return cacheable; }
    
    @Override
    public String toString() {
        return name;
    }
}

