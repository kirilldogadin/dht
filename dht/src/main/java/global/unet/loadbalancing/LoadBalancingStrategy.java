package global.unet.loadbalancing;

/**
 * Стратегии балансировки нагрузки
 */
public enum LoadBalancingStrategy {
    
    /**
     * Круговая балансировка (Round Robin)
     */
    ROUND_ROBIN("Round Robin", "Равномерное распределение запросов по узлам"),
    
    /**
     * Взвешенная круговая балансировка
     */
    WEIGHTED_ROUND_ROBIN("Weighted Round Robin", "Распределение с учетом весов узлов"),
    
    /**
     * Наименьшее количество соединений
     */
    LEAST_CONNECTIONS("Least Connections", "Выбор узла с наименьшим количеством соединений"),
    
    /**
     * Наименьшее время отклика
     */
    LEAST_RESPONSE_TIME("Least Response Time", "Выбор узла с наименьшим временем отклика"),
    
    /**
     * Наименьшая нагрузка
     */
    LEAST_LOAD("Least Load", "Выбор узла с наименьшей общей нагрузкой"),
    
    /**
     * Случайный выбор
     */
    RANDOM("Random", "Случайный выбор узла"),
    
    /**
     * Взвешенный случайный выбор
     */
    WEIGHTED_RANDOM("Weighted Random", "Случайный выбор с учетом весов"),
    
    /**
     * Адаптивная балансировка
     */
    ADAPTIVE("Adaptive", "Динамическая адаптация стратегии на основе условий сети");
    
    private final String name;
    private final String description;
    
    LoadBalancingStrategy(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    @Override
    public String toString() {
        return name;
    }
}

