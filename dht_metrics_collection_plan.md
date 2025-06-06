# План Системы Сбора Метрик и Логирования для DHT

## 1. Структура Агента Метрик

### Основные Классы

```java
/**
 * Главный класс агента метрик для DHT узла
 */
public class DHTMetricsAgent {
    private final SimpleDHTNode node;
    private final String nodeId;
    private final MetricsRegistry registry;
    private final MetricsExporter exporter;
    private final MetricsSender sender;
    private final ScheduledExecutorService scheduler;
    
    /**
     * Конструктор агента метрик
     * @param node DHT узел для мониторинга
     * @param monitoringServerUrl URL сервера мониторинга
     */
    public DHTMetricsAgent(SimpleDHTNode node, String monitoringServerUrl) {
        this.node = node;
        this.nodeId = node.getNodeId().toString();
        this.registry = new MetricsRegistry();
        this.exporter = new PrometheusMetricsExporter(registry);
        this.sender = new HttpMetricsSender(monitoringServerUrl);
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        // Регистрация базовых метрик
        registerNodeMetrics();
        registerRPCMetrics();
        registerNetworkMetrics();
        registerPerformanceMetrics();
    }
    
    /**
     * Запуск агента метрик
     */
    public void start() {
        // Сбор метрик каждые 5 секунд
        scheduler.scheduleAtFixedRate(this::collectAndSendMetrics, 0, 5, TimeUnit.SECONDS);
    }
    
    /**
     * Остановка агента метрик
     */
    public void stop() {
        scheduler.shutdown();
    }
    
    /**
     * Сбор и отправка метрик
     */
    private void collectAndSendMetrics() {
        try {
            // Обновление значений метрик
            updateNodeMetrics();
            updateRPCMetrics();
            updateNetworkMetrics();
            updatePerformanceMetrics();
            
            // Экспорт метрик
            String prometheusMetrics = exporter.export();
            
            // Отправка метрик на сервер
            sender.sendMetrics(nodeId, prometheusMetrics);
        } catch (Exception e) {
            LogManager.getLogger().error("Error collecting or sending metrics", e);
        }
    }
    
    /**
     * Регистрация метрик узла
     */
    private void registerNodeMetrics() {
        registry.registerGauge("node_uptime_seconds", "Время работы узла в секундах");
        registry.registerGauge("node_routing_table_size", "Размер таблицы маршрутизации");
        registry.registerGauge("node_storage_size", "Количество хранимых ключей");
        registry.registerGauge("node_memory_usage_bytes", "Использование памяти в байтах");
        registry.registerGauge("node_cpu_usage_percent", "Использование CPU в процентах");
    }
    
    /**
     * Регистрация метрик RPC
     */
    private void registerRPCMetrics() {
        registry.registerCounter("rpc_requests_total", "Общее количество RPC запросов", "type");
        registry.registerHistogram("rpc_response_time_ms", "Время отклика RPC операций в мс", "type");
        registry.registerCounter("rpc_errors_total", "Количество ошибок RPC", "type", "error");
        registry.registerCounter("rpc_timeouts_total", "Количество таймаутов RPC", "type");
        registry.registerGauge("rpc_success_rate_percent", "Процент успешных RPC операций", "type");
    }
    
    /**
     * Регистрация метрик сети
     */
    private void registerNetworkMetrics() {
        registry.registerGauge("network_connections", "Количество активных соединений");
        registry.registerGauge("network_peers", "Количество известных узлов");
        registry.registerHistogram("network_latency_ms", "Задержка до других узлов в мс", "peer");
        registry.registerCounter("network_bytes_sent", "Количество отправленных байт");
        registry.registerCounter("network_bytes_received", "Количество полученных байт");
    }
    
    /**
     * Регистрация метрик производительности
     */
    private void registerPerformanceMetrics() {
        registry.registerCounter("lookup_operations_total", "Количество операций поиска");
        registry.registerHistogram("lookup_hop_count", "Количество хопов при поиске");
        registry.registerGauge("lookup_success_rate_percent", "Процент успешных поисков");
        registry.registerCounter("storage_operations_total", "Количество операций с хранилищем", "operation");
    }
    
    /**
     * Обновление метрик узла
     */
    private void updateNodeMetrics() {
        long uptime = (System.currentTimeMillis() - node.getStartTime()) / 1000;
        registry.setGauge("node_uptime_seconds", uptime);
        registry.setGauge("node_routing_table_size", node.getRoutingTable().size());
        registry.setGauge("node_storage_size", node.getLocalStorage().size());
        
        // Получение системных метрик
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        registry.setGauge("node_memory_usage_bytes", usedMemory);
        
        // CPU использование (примерное)
        double cpuUsage = getCPUUsage();
        registry.setGauge("node_cpu_usage_percent", cpuUsage);
    }
    
    /**
     * Обновление метрик RPC
     */
    private void updateRPCMetrics() {
        // Метрики уже обновляются в реальном времени через RPCMetricsCollector
    }
    
    /**
     * Обновление метрик сети
     */
    private void updateNetworkMetrics() {
        registry.setGauge("network_peers", node.getRoutingTable().getAllNodes().size());
        
        // Обновление задержки до известных узлов
        for (NodeInfo peer : node.getRoutingTable().getAllNodes()) {
            long latency = measureLatency(peer);
            registry.observeHistogram("network_latency_ms", latency, peer.getId().toString());
        }
    }
    
    /**
     * Обновление метрик производительности
     */
    private void updatePerformanceMetrics() {
        // Метрики уже обновляются в реальном времени через PerformanceMetricsCollector
    }
    
    /**
     * Получение использования CPU
     */
    private double getCPUUsage() {
        // Упрощенная реализация для демонстрации
        return ThreadMXBean.getCurrentThreadCpuTime() / 1_000_000.0;
    }
    
    /**
     * Измерение задержки до узла
     */
    private long measureLatency(NodeInfo peer) {
        try {
            long start = System.currentTimeMillis();
            node.ping(peer);
            return System.currentTimeMillis() - start;
        } catch (Exception e) {
            return -1; // Ошибка измерения
        }
    }
}
```

### Регистр Метрик

```java
/**
 * Регистр метрик для хранения и управления метриками
 */
public class MetricsRegistry {
    private final Map<String, Metric> metrics = new ConcurrentHashMap<>();
    
    /**
     * Регистрация счетчика
     */
    public Counter registerCounter(String name, String description, String... labelNames) {
        Counter counter = new Counter(name, description, labelNames);
        metrics.put(name, counter);
        return counter;
    }
    
    /**
     * Регистрация измерителя
     */
    public Gauge registerGauge(String name, String description, String... labelNames) {
        Gauge gauge = new Gauge(name, description, labelNames);
        metrics.put(name, gauge);
        return gauge;
    }
    
    /**
     * Регистрация гистограммы
     */
    public Histogram registerHistogram(String name, String description, String... labelNames) {
        Histogram histogram = new Histogram(name, description, labelNames);
        metrics.put(name, histogram);
        return histogram;
    }
    
    /**
     * Получение всех метрик
     */
    public Collection<Metric> getMetrics() {
        return metrics.values();
    }
    
    /**
     * Получение метрики по имени
     */
    public Metric getMetric(String name) {
        return metrics.get(name);
    }
    
    /**
     * Установка значения измерителя
     */
    public void setGauge(String name, double value, String... labelValues) {
        Metric metric = metrics.get(name);
        if (metric instanceof Gauge) {
            ((Gauge) metric).set(value, labelValues);
        }
    }
    
    /**
     * Увеличение счетчика
     */
    public void incrementCounter(String name, String... labelValues) {
        incrementCounter(name, 1, labelValues);
    }
    
    /**
     * Увеличение счетчика на заданное значение
     */
    public void incrementCounter(String name, double value, String... labelValues) {
        Metric metric = metrics.get(name);
        if (metric instanceof Counter) {
            ((Counter) metric).increment(value, labelValues);
        }
    }
    
    /**
     * Наблюдение значения для гистограммы
     */
    public void observeHistogram(String name, double value, String... labelValues) {
        Metric metric = metrics.get(name);
        if (metric instanceof Histogram) {
            ((Histogram) metric).observe(value, labelValues);
        }
    }
}
```

### Экспортер Метрик

```java
/**
 * Экспортер метрик в формате Prometheus
 */
public class PrometheusMetricsExporter implements MetricsExporter {
    private final MetricsRegistry registry;
    
    public PrometheusMetricsExporter(MetricsRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * Экспорт метрик в формате Prometheus
     */
    @Override
    public String export() {
        StringBuilder sb = new StringBuilder();
        
        for (Metric metric : registry.getMetrics()) {
            // Добавление комментария с описанием
            sb.append("# HELP ").append(metric.getName()).append(" ").append(metric.getDescription()).append("\n");
            
            // Добавление типа метрики
            sb.append("# TYPE ").append(metric.getName()).append(" ").append(getType(metric)).append("\n");
            
            // Добавление значений метрики
            for (MetricSample sample : metric.getSamples()) {
                sb.append(metric.getName());
                
                // Добавление меток
                if (!sample.getLabels().isEmpty()) {
                    sb.append("{");
                    boolean first = true;
                    for (Map.Entry<String, String> label : sample.getLabels().entrySet()) {
                        if (!first) {
                            sb.append(",");
                        }
                        sb.append(label.getKey()).append("=\"").append(label.getValue()).append("\"");
                        first = false;
                    }
                    sb.append("}");
                }
                
                // Добавление значения
                sb.append(" ").append(sample.getValue()).append("\n");
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Получение типа метрики для Prometheus
     */
    private String getType(Metric metric) {
        if (metric instanceof Counter) {
            return "counter";
        } else if (metric instanceof Gauge) {
            return "gauge";
        } else if (metric instanceof Histogram) {
            return "histogram";
        } else {
            return "untyped";
        }
    }
}
```

### Отправитель Метрик

```java
/**
 * Отправитель метрик по HTTP
 */
public class HttpMetricsSender implements MetricsSender {
    private final String serverUrl;
    private final HttpClient httpClient;
    
    public HttpMetricsSender(String serverUrl) {
        this.serverUrl = serverUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }
    
    /**
     * Отправка метрик на сервер мониторинга
     */
    @Override
    public void sendMetrics(String nodeId, String metrics) throws Exception {
        // Создание JSON с метриками
        String json = String.format(
            "{\"node_id\": \"%s\", \"timestamp\": %d, \"metrics\": %s}",
            nodeId,
            System.currentTimeMillis(),
            escapeMetrics(metrics)
        );
        
        // Создание HTTP запроса
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(serverUrl + "/api/metrics"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        // Отправка запроса
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Проверка ответа
        if (response.statusCode() != 200) {
            throw new IOException("Failed to send metrics: " + response.statusCode() + " " + response.body());
        }
    }
    
    /**
     * Экранирование метрик для JSON
     */
    private String escapeMetrics(String metrics) {
        return "\"" + metrics.replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
}
```

## 2. Интеграция с RPC Операциями

```java
/**
 * Сборщик метрик для RPC операций
 */
public class RPCMetricsCollector {
    private final MetricsRegistry registry;
    
    public RPCMetricsCollector(MetricsRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * Запись начала RPC операции
     */
    public void recordRPCStart(MessageType type) {
        registry.incrementCounter("rpc_requests_total", type.name());
    }
    
    /**
     * Запись завершения RPC операции
     */
    public void recordRPCComplete(MessageType type, long durationMs, boolean success) {
        registry.observeHistogram("rpc_response_time_ms", durationMs, type.name());
        
        if (!success) {
            registry.incrementCounter("rpc_errors_total", type.name(), "general");
        }
        
        // Обновление процента успешных операций
        updateSuccessRate(type);
    }
    
    /**
     * Запись таймаута RPC операции
     */
    public void recordRPCTimeout(MessageType type) {
        registry.incrementCounter("rpc_timeouts_total", type.name());
        registry.incrementCounter("rpc_errors_total", type.name(), "timeout");
        
        // Обновление процента успешных операций
        updateSuccessRate(type);
    }
    
    /**
     * Обновление процента успешных операций
     */
    private void updateSuccessRate(MessageType type) {
        String typeName = type.name();
        
        // Получение счетчиков
        Counter requestsCounter = (Counter) registry.getMetric("rpc_requests_total");
        Counter errorsCounter = (Counter) registry.getMetric("rpc_errors_total");
        
        // Расчет процента успешных операций
        double totalRequests = requestsCounter.getValue(typeName);
        double totalErrors = 0;
        
        for (MetricSample sample : errorsCounter.getSamples()) {
            if (sample.getLabels().get("type").equals(typeName)) {
                totalErrors += sample.getValue();
            }
        }
        
        double successRate = totalRequests > 0 ? 100 * (1 - totalErrors / totalRequests) : 100;
        registry.setGauge("rpc_success_rate_percent", successRate, typeName);
    }
}
```

## 3. Интеграция с SimpleDHTNode

```java
/**
 * Модификация SimpleDHTNode для поддержки мониторинга
 */
public class SimpleDHTNode {
    // Существующие поля...
    
    private DHTMetricsAgent metricsAgent;
    private RPCMetricsCollector rpcMetricsCollector;
    private PerformanceMetricsCollector performanceMetricsCollector;
    private boolean monitoringEnabled;
    private long startTime;
    
    /**
     * Конструктор с поддержкой мониторинга
     */
    public SimpleDHTNode(int port, boolean enableMonitoring) throws Exception {
        // Существующая инициализация...
        
        this.startTime = System.currentTimeMillis();
        this.monitoringEnabled = enableMonitoring;
        
        if (enableMonitoring) {
            // Создание сборщиков метрик
            MetricsRegistry registry = new MetricsRegistry();
            this.rpcMetricsCollector = new RPCMetricsCollector(registry);
            this.performanceMetricsCollector = new PerformanceMetricsCollector(registry);
            
            // Создание агента метрик
            this.metricsAgent = new DHTMetricsAgent(this, "http://monitoring-server:8080");
            
            // Запуск агента
            this.metricsAgent.start();
        }
    }
    
    /**
     * Получение времени запуска узла
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Модифицированный метод отправки RPC сообщения с метриками
     */
    public CompletableFuture<DHTMessage> sendMessage(NodeInfo target, DHTMessage message) {
        if (monitoringEnabled) {
            rpcMetricsCollector.recordRPCStart(message.getType());
            
            long startTime = System.currentTimeMillis();
            
            return rpcClient.sendMessage(target, message)
                .whenComplete((response, throwable) -> {
                    long duration = System.currentTimeMillis() - startTime;
                    boolean success = throwable == null;
                    
                    if (throwable instanceof TimeoutException) {
                        rpcMetricsCollector.recordRPCTimeout(message.getType());
                    } else {
                        rpcMetricsCollector.recordRPCComplete(message.getType(), duration, success);
                    }
                });
        } else {
            return rpcClient.sendMessage(target, message);
        }
    }
    
    /**
     * Модифицированный метод поиска с метриками
     */
    public List<NodeInfo> findNode(KademliaId targetId) {
        if (monitoringEnabled) {
            performanceMetricsCollector.recordLookupStart();
            
            try {
                List<NodeInfo> result = iterativeLookup.findNode(targetId);
                performanceMetricsCollector.recordLookupComplete(true, iterativeLookup.getLastHopCount());
                return result;
            } catch (Exception e) {
                performanceMetricsCollector.recordLookupComplete(false, iterativeLookup.getLastHopCount());
                throw e;
            }
        } else {
            return iterativeLookup.findNode(targetId);
        }
    }
    
    /**
     * Модифицированный метод сохранения с метриками
     */
    public boolean store(KademliaId key, String value) {
        if (monitoringEnabled) {
            performanceMetricsCollector.recordStorageOperation("store");
        }
        
        // Существующая логика...
        return true;
    }
    
    /**
     * Остановка узла с остановкой мониторинга
     */
    public void shutdown() {
        if (monitoringEnabled && metricsAgent != null) {
            metricsAgent.stop();
        }
        
        // Существующая логика остановки...
    }
}
```

## 4. Система Логирования

```java
/**
 * Расширенная система логирования для DHT
 */
public class DHTLogger {
    private static final Logger logger = LogManager.getLogger(DHTLogger.class);
    private final SimpleDHTNode node;
    private final String nodeId;
    private final LogExporter exporter;
    
    public DHTLogger(SimpleDHTNode node, String logServerUrl) {
        this.node = node;
        this.nodeId = node.getNodeId().toString();
        this.exporter = new HttpLogExporter(logServerUrl);
    }
    
    /**
     * Логирование информационного сообщения
     */
    public void info(String message) {
        logger.info(message);
        exportLog("INFO", message);
    }
    
    /**
     * Логирование предупреждения
     */
    public void warn(String message) {
        logger.warn(message);
        exportLog("WARN", message);
    }
    
    /**
     * Логирование ошибки
     */
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        exportLog("ERROR", message + ": " + throwable.getMessage());
    }
    
    /**
     * Логирование RPC операции
     */
    public void logRPCOperation(MessageType type, NodeInfo target, boolean success) {
        String message = String.format("RPC %s to %s: %s", 
            type, target.getId(), success ? "SUCCESS" : "FAILED");
        
        logger.debug(message);
        exportLog("RPC", message);
    }
    
    /**
     * Логирование операции поиска
     */
    public void logLookup(KademliaId targetId, int hopCount, boolean success) {
        String message = String.format("Lookup for %s: %s (hops: %d)", 
            targetId, success ? "SUCCESS" : "FAILED", hopCount);
        
        logger.debug(message);
        exportLog("LOOKUP", message);
    }
    
    /**
     * Экспорт лога на сервер
     */
    private void exportLog(String level, String message) {
        try {
            LogEntry entry = new LogEntry(
                nodeId,
                System.currentTimeMillis(),
                level,
                message
            );
            
            exporter.exportLog(entry);
        } catch (Exception e) {
            logger.error("Failed to export log", e);
        }
    }
}
```

## 5. Интеграция с Существующими Тестами

```java
/**
 * Расширение тестов для проверки метрик
 */
public class MetricsEnabledTest {
    private SimpleDHTNode node;
    private DHTMetricsAgent metricsAgent;
    private MetricsRegistry registry;
    
    @Before
    public void setUp() {
        // Создание узла с включенным мониторингом
        node = new SimpleDHTNode(8080, true);
        
        // Получение доступа к метрикам
        metricsAgent = node.getMetricsAgent();
        registry = metricsAgent.getRegistry();
    }
    
    @Test
    public void testRPCMetrics() {
        // Выполнение RPC операций
        NodeInfo target = new NodeInfo(generateRandomNodeId(), "localhost", 8081);
        node.ping(target);
        
        // Проверка метрик
        Counter requestsCounter = (Counter) registry.getMetric("rpc_requests_total");
        double pingRequests = requestsCounter.getValue("PING");
        
        Assert.assertTrue("Should have recorded PING requests", pingRequests > 0);
    }
    
    @Test
    public void testLookupMetrics() {
        // Выполнение поиска
        KademliaId targetId = generateRandomNodeId();
        node.findNode(targetId);
        
        // Проверка метрик
        Counter lookupCounter = (Counter) registry.getMetric("lookup_operations_total");
        double lookups = lookupCounter.getValue();
        
        Assert.assertTrue("Should have recorded lookup operations", lookups > 0);
    }
}
```

## 6. Конфигурация Мониторинга

```java
/**
 * Конфигурация мониторинга
 */
public class MonitoringConfig {
    private boolean enabled;
    private String serverUrl;
    private int collectionIntervalSeconds;
    private boolean exportPrometheus;
    private boolean exportLogs;
    private LogLevel minimumLogLevel;
    
    /**
     * Конструктор с настройками по умолчанию
     */
    public MonitoringConfig() {
        this.enabled = false;
        this.serverUrl = "http://localhost:8080";
        this.collectionIntervalSeconds = 5;
        this.exportPrometheus = true;
        this.exportLogs = true;
        this.minimumLogLevel = LogLevel.INFO;
    }
    
    // Геттеры и сеттеры...
    
    /**
     * Создание конфигурации из файла
     */
    public static MonitoringConfig fromFile(String path) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(path));
        
        MonitoringConfig config = new MonitoringConfig();
        config.setEnabled(Boolean.parseBoolean(props.getProperty("monitoring.enabled", "false")));
        config.setServerUrl(props.getProperty("monitoring.server.url", "http://localhost:8080"));
        config.setCollectionIntervalSeconds(Integer.parseInt(props.getProperty("monitoring.interval.seconds", "5")));
        config.setExportPrometheus(Boolean.parseBoolean(props.getProperty("monitoring.export.prometheus", "true")));
        config.setExportLogs(Boolean.parseBoolean(props.getProperty("monitoring.export.logs", "true")));
        config.setMinimumLogLevel(LogLevel.valueOf(props.getProperty("monitoring.log.level", "INFO")));
        
        return config;
    }
}
```

## 7. Ожидаемые Результаты

Реализация этой системы сбора метрик и логирования позволит:

1. **Собирать детальные метрики** о работе каждого DHT узла
2. **Отслеживать производительность** RPC операций в реальном времени
3. **Анализировать эффективность** алгоритмов поиска и маршрутизации
4. **Централизованно хранить логи** для отладки и анализа проблем
5. **Экспортировать метрики** в стандартном формате Prometheus
6. **Интегрироваться с существующим кодом** без значительных изменений
7. **Настраивать уровень мониторинга** в зависимости от окружения

Эта система будет основой для создания полноценной инфраструктуры мониторинга DHT сети.

