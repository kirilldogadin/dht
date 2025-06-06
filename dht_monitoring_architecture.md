# Архитектура Системы Мониторинга DHT

## Обзор Архитектуры

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   DHT Node 1    │    │   DHT Node 2    │    │   DHT Node N    │
│                 │    │                 │    │                 │
│ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ │ Metrics     │ │    │ │ Metrics     │ │    │ │ Metrics     │ │
│ │ Agent       │ │    │ │ Agent       │ │    │ │ Agent       │ │
│ └─────────────┘ │    │ └─────────────┘ │    │ └─────────────┘ │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          │ HTTP/JSON            │ HTTP/JSON            │ HTTP/JSON
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Monitoring Server      │
                    │                         │
                    │ ┌─────────────────────┐ │
                    │ │   REST API          │ │
                    │ │   /api/metrics      │ │
                    │ │   /api/nodes        │ │
                    │ │   /api/network      │ │
                    │ └─────────────────────┘ │
                    │                         │
                    │ ┌─────────────────────┐ │
                    │ │   Data Storage      │ │
                    │ │   SQLite/PostgreSQL │ │
                    │ └─────────────────────┘ │
                    │                         │
                    │ ┌─────────────────────┐ │
                    │ │   Data Aggregation  │ │
                    │ │   & Processing      │ │
                    │ └─────────────────────┘ │
                    └────────────┬────────────┘
                                 │
                                 │ HTTP/WebSocket
                                 │
                    ┌────────────▼────────────┐
                    │   Web Dashboard         │
                    │                         │
                    │ ┌─────────────────────┐ │
                    │ │ Real-time Charts    │ │
                    │ │ Network Topology    │ │
                    │ │ Performance Metrics │ │
                    │ │ Log Viewer          │ │
                    │ └─────────────────────┘ │
                    └─────────────────────────┘
```

## Ключевые Метрики для Сбора

### 1. Метрики Узла (Node Metrics)
- **node_id**: Уникальный идентификатор узла
- **node_uptime**: Время работы узла
- **node_status**: Статус узла (active, joining, leaving)
- **node_address**: IP адрес и порт узла
- **routing_table_size**: Размер таблицы маршрутизации
- **local_storage_size**: Количество хранимых ключей
- **memory_usage**: Использование памяти
- **cpu_usage**: Использование CPU

### 2. Метрики RPC Операций (RPC Metrics)
- **rpc_requests_total**: Общее количество RPC запросов
- **rpc_requests_by_type**: Запросы по типам (PING, STORE, FIND_NODE, FIND_VALUE)
- **rpc_response_time**: Время отклика RPC операций
- **rpc_success_rate**: Процент успешных операций
- **rpc_errors_total**: Количество ошибок по типам
- **rpc_timeouts_total**: Количество таймаутов
- **rpc_bandwidth_usage**: Использование пропускной способности

### 3. Метрики Сети (Network Metrics)
- **network_connections**: Активные соединения
- **network_peers**: Количество известных узлов
- **network_latency**: Задержка до других узлов
- **network_packet_loss**: Потери пакетов
- **network_throughput**: Пропускная способность
- **network_topology_changes**: Изменения в топологии

### 4. Метрики Производительности (Performance Metrics)
- **lookup_operations_total**: Количество операций поиска
- **lookup_success_rate**: Успешность операций поиска
- **lookup_hop_count**: Среднее количество хопов при поиске
- **storage_operations_total**: Операции с хранилищем
- **data_replication_factor**: Коэффициент репликации
- **load_balancing_efficiency**: Эффективность балансировки

## Компоненты Системы

### 1. Metrics Agent (Java)
```java
public class DHTMetricsAgent {
    private final SimpleDHTNode node;
    private final MetricsCollector collector;
    private final MetricsExporter exporter;
    
    // Сбор метрик каждые N секунд
    public void collectMetrics();
    
    // Экспорт в Prometheus формате
    public String exportPrometheusMetrics();
    
    // Отправка на сервер мониторинга
    public void sendToMonitoringServer();
}
```

### 2. Monitoring Server (Python/Flask)
```python
class MonitoringServer:
    def __init__(self):
        self.app = Flask(__name__)
        self.db = MetricsDatabase()
        self.aggregator = DataAggregator()
    
    # API endpoints
    @app.route('/api/metrics', methods=['POST'])
    def receive_metrics():
        # Получение метрик от узлов
        pass
    
    @app.route('/api/nodes')
    def get_nodes():
        # Список активных узлов
        pass
    
    @app.route('/api/network/topology')
    def get_topology():
        # Топология сети
        pass
```

### 3. Web Dashboard (React)
```jsx
const DHTDashboard = () => {
    return (
        <div className="dashboard">
            <NetworkTopology />
            <RealTimeMetrics />
            <PerformanceCharts />
            <LogViewer />
            <AlertsPanel />
        </div>
    );
};
```

## Технологический Стек

### Backend
- **Java**: Интеграция с существующим DHT кодом
- **Python Flask**: REST API сервер мониторинга
- **SQLite/PostgreSQL**: Хранение метрик и логов
- **WebSocket**: Реальное время для дашборда

### Frontend
- **React**: Веб-интерфейс дашборда
- **Recharts**: Графики и диаграммы
- **D3.js**: Визуализация топологии сети
- **Socket.io**: WebSocket клиент

### Развертывание
- **Docker**: Контейнеризация компонентов
- **Docker Compose**: Оркестрация сервисов
- **Nginx**: Reverse proxy для веб-интерфейса

## План Развертывания Стендов

### Стенд 1: Development
- 3-5 DHT узлов локально
- Локальный сервер мониторинга
- Веб-дашборд на localhost

### Стенд 2: Testing
- 10-20 DHT узлов в контейнерах
- Централизованный сервер мониторинга
- Публичный веб-интерфейс

### Стенд 3: Production
- Масштабируемая сеть DHT узлов
- Высокодоступный сервер мониторинга
- Система алертов и уведомлений

## Интеграция с Существующим Кодом

### 1. Модификация SimpleDHTNode
```java
public class SimpleDHTNode {
    private DHTMetricsAgent metricsAgent;
    
    public SimpleDHTNode(int port, boolean enableMonitoring) {
        // Существующий код...
        
        if (enableMonitoring) {
            this.metricsAgent = new DHTMetricsAgent(this);
            this.metricsAgent.start();
        }
    }
}
```

### 2. Инструментация RPC Операций
```java
public class RPCManager {
    private MetricsCollector metrics;
    
    public CompletableFuture<DHTMessage> sendMessage(NodeInfo target, DHTMessage message) {
        long startTime = System.currentTimeMillis();
        
        return rpcClient.sendMessage(target, message)
            .whenComplete((response, throwable) -> {
                long duration = System.currentTimeMillis() - startTime;
                metrics.recordRPCOperation(message.getType(), duration, throwable == null);
            });
    }
}
```

## Ожидаемые Результаты

1. **Полная видимость** в работу DHT сети
2. **Реальное время** мониторинга производительности
3. **Историческая аналитика** для оптимизации
4. **Автоматические алерты** при проблемах
5. **Удобный интерфейс** для отладки и анализа

Эта архитектура обеспечит полноценную инфраструктуру для мониторинга и отладки DHT сети на всех этапах разработки.

