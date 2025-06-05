# Package reports

Пакет `reports` предназначен для создания, управления и отображения отчетов о статусе проектов в системе DHT.

## Структура пакета

- `ReportManager.java` - основной класс для управления отчетами
- `ProjectStatusReport.java` - класс для представления отчета о статусе проекта
- `html/` - подпакет для работы с HTML отчетами
  - `HtmlReportGenerator.java` - генератор HTML отчетов

## Ресурсы

HTML отчеты хранятся в директории `src/main/resources/reports/`.

## Использование

### Получение списка доступных отчетов

```java
ReportManager reportManager = new ReportManager();
List<String> reports = reportManager.getAvailableReports();
```

### Получение содержимого HTML отчета

```java
ReportManager reportManager = new ReportManager();
String htmlContent = reportManager.getHtmlReport("dht_report.html");
```

### Создание нового отчета о статусе проекта

```java
ProjectStatusReport report = new ProjectStatusReport("DHT", "Distributed Hash Table")
    .setProgressPercentage(35)
    .addComponent("XOR метрика", ComponentStatus.IN_PROGRESS, "Реализация XOR метрики для Kademlia", 70)
    .addComponent("K-buckets", ComponentStatus.NOT_STARTED, "Реализация k-buckets с LRU логикой", 0)
    .addIssue("Метод computeDistanceAsUnionId возвращает null", "Необходимо реализовать метод", IssueSeverity.HIGH)
    .addRecommendation("Начать с реализации XOR метрики");
```

### Генерация HTML отчета

```java
HtmlReportGenerator generator = new HtmlReportGenerator("Отчет о статусе DHT", "Анализ текущего состояния проекта DHT");
generator.addSection("overview", "<h2>Обзор проекта</h2><p>Проект DHT находится в стадии разработки...</p>");
generator.addSection("components", "<h2>Компоненты</h2><ul><li>XOR метрика: 70%</li><li>K-buckets: 0%</li></ul>");
String htmlReport = generator.generate();
```

## Интеграция с GitHub

Пакет `reports` может быть интегрирован с GitHub для отслеживания задач и их статуса. Для этого можно использовать GitHub API через библиотеку, такую как [GitHub API for Java](https://github-api.kohsuke.org/).

