package global.unet.demo;

import global.unet.network.SimpleDHTNode;
import global.unet.network.ScalableDHTNode;
import global.unet.domain.structures.NodeInfo;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Демонстрационный стенд для тестирования DHT системы
 * Показывает работу базовых и продвинутых функций
 */
public class DHTDemoStand {
    private static final Logger logger = Logger.getLogger(DHTDemoStand.class.getName());
    
    private final List<SimpleDHTNode> simpleNodes;
    private final List<ScalableDHTNode> scalableNodes;
    private final Random random;
    
    public DHTDemoStand() {
        this.simpleNodes = new ArrayList<>();
        this.scalableNodes = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Главный метод для запуска демонстрации
     */
    public static void main(String[] args) {
        DHTDemoStand demo = new DHTDemoStand();
        
        System.out.println("🚀 Запуск демонстрационного стенда DHT системы");
        System.out.println("=" * 60);
        
        try {
            // Демонстрация базовой DHT сети
            demo.demonstrateBasicDHT();
            
            Thread.sleep(2000);
            
            // Демонстрация масштабируемой DHT сети
            demo.demonstrateScalableDHT();
            
            Thread.sleep(2000);
            
            // Демонстрация отказоустойчивости
            demo.demonstrateFaultTolerance();
            
            Thread.sleep(2000);
            
            // Демонстрация производительности
            demo.demonstratePerformance();
            
        } catch (Exception e) {
            logger.severe("Ошибка в демонстрации: " + e.getMessage());
        } finally {
            demo.cleanup();
        }
        
        System.out.println("✅ Демонстрация завершена");
    }
    
    /**
     * Демонстрация базовой DHT сети
     */
    public void demonstrateBasicDHT() {
        System.out.println("\n📊 ДЕМОНСТРАЦИЯ БАЗОВОЙ DHT СЕТИ");
        System.out.println("-" * 40);
        
        // Создание 5 простых узлов
        System.out.println("Создание 5 DHT узлов...");
        for (int i = 0; i < 5; i++) {
            SimpleDHTNode node = new SimpleDHTNode(8080 + i);
            node.start();
            simpleNodes.add(node);
            System.out.println("✅ Узел " + (i + 1) + " запущен на порту " + (8080 + i));
        }
        
        // Соединение узлов в сеть
        System.out.println("\nСоединение узлов в сеть...");
        connectSimpleNodes();
        
        // Тестирование сохранения и поиска данных
        System.out.println("\nТестирование сохранения данных:");
        testBasicOperations();
        
        // Вывод статистики
        System.out.println("\nСтатистика базовой сети:");
        printSimpleNodesStatistics();
    }
    
    /**
     * Демонстрация масштабируемой DHT сети
     */
    public void demonstrateScalableDHT() {
        System.out.println("\n🚀 ДЕМОНСТРАЦИЯ МАСШТАБИРУЕМОЙ DHT СЕТИ");
        System.out.println("-" * 45);
        
        // Создание 3 масштабируемых узлов
        System.out.println("Создание 3 масштабируемых DHT узлов...");
        for (int i = 0; i < 3; i++) {
            ScalableDHTNode node = new ScalableDHTNode(9080 + i);
            node.start();
            scalableNodes.add(node);
            System.out.println("✅ Масштабируемый узел " + (i + 1) + " запущен на порту " + (9080 + i));
        }
        
        // Соединение узлов
        System.out.println("\nСоединение масштабируемых узлов...");
        connectScalableNodes();
        
        // Тестирование продвинутых функций
        System.out.println("\nТестирование продвинутых функций:");
        testAdvancedOperations();
        
        // Вывод статистики
        System.out.println("\nСтатистика масштабируемой сети:");
        printScalableNodesStatistics();
    }
    
    /**
     * Демонстрация отказоустойчивости
     */
    public void demonstrateFaultTolerance() {
        System.out.println("\n🛡️ ДЕМОНСТРАЦИЯ ОТКАЗОУСТОЙЧИВОСТИ");
        System.out.println("-" * 35);
        
        if (scalableNodes.size() < 3) {
            System.out.println("❌ Недостаточно узлов для демонстрации отказоустойчивости");
            return;
        }
        
        // Сохранение данных
        System.out.println("Сохранение тестовых данных...");
        ScalableDHTNode firstNode = scalableNodes.get(0);
        for (int i = 0; i < 10; i++) {
            String key = "fault-test-" + i;
            String value = "Данные для тестирования отказоустойчивости " + i;
            firstNode.store(key, value);
        }
        
        // Имитация отказа узла
        System.out.println("\n⚠️ Имитация отказа узла...");
        ScalableDHTNode failedNode = scalableNodes.get(1);
        System.out.println("Останавливаем узел " + failedNode.getNodeId());
        failedNode.stop();
        
        // Проверка доступности данных
        System.out.println("\nПроверка доступности данных после отказа:");
        ScalableDHTNode workingNode = scalableNodes.get(2);
        int foundData = 0;
        for (int i = 0; i < 10; i++) {
            String key = "fault-test-" + i;
            Object result = workingNode.find(key);
            if (result != null) {
                foundData++;
                System.out.println("✅ Данные найдены: " + key);
            } else {
                System.out.println("❌ Данные потеряны: " + key);
            }
        }
        
        System.out.println(String.format("\n📊 Результат: %d из 10 записей сохранились (%.1f%%)", 
            foundData, (double) foundData / 10 * 100));
        
        // Восстановление узла
        System.out.println("\n🔄 Восстановление узла...");
        ScalableDHTNode newNode = new ScalableDHTNode(failedNode.getPort());
        newNode.start();
        scalableNodes.set(1, newNode);
        System.out.println("✅ Узел восстановлен");
    }
    
    /**
     * Демонстрация производительности
     */
    public void demonstratePerformance() {
        System.out.println("\n⚡ ДЕМОНСТРАЦИЯ ПРОИЗВОДИТЕЛЬНОСТИ");
        System.out.println("-" * 35);
        
        if (scalableNodes.isEmpty()) {
            System.out.println("❌ Нет доступных узлов для тестирования производительности");
            return;
        }
        
        ScalableDHTNode testNode = scalableNodes.get(0);
        
        // Тест производительности записи
        System.out.println("Тестирование производительности записи...");
        long startTime = System.currentTimeMillis();
        int writeOperations = 100;
        
        for (int i = 0; i < writeOperations; i++) {
            String key = "perf-test-" + i;
            String value = "Тестовые данные для измерения производительности " + i;
            testNode.store(key, value);
        }
        
        long writeTime = System.currentTimeMillis() - startTime;
        double writeOpsPerSec = (double) writeOperations / writeTime * 1000;
        
        System.out.println(String.format("📝 Записано %d операций за %d мс (%.1f ops/sec)", 
            writeOperations, writeTime, writeOpsPerSec));
        
        // Тест производительности чтения
        System.out.println("\nТестирование производительности чтения...");
        startTime = System.currentTimeMillis();
        int readOperations = 100;
        int foundOperations = 0;
        
        for (int i = 0; i < readOperations; i++) {
            String key = "perf-test-" + i;
            Object result = testNode.find(key);
            if (result != null) {
                foundOperations++;
            }
        }
        
        long readTime = System.currentTimeMillis() - startTime;
        double readOpsPerSec = (double) readOperations / readTime * 1000;
        
        System.out.println(String.format("🔍 Прочитано %d операций за %d мс (%.1f ops/sec)", 
            readOperations, readTime, readOpsPerSec));
        System.out.println(String.format("✅ Найдено %d из %d записей (%.1f%%)", 
            foundOperations, readOperations, (double) foundOperations / readOperations * 100));
    }
    
    /**
     * Соединение простых узлов в сеть
     */
    private void connectSimpleNodes() {
        for (int i = 1; i < simpleNodes.size(); i++) {
            SimpleDHTNode currentNode = simpleNodes.get(i);
            SimpleDHTNode previousNode = simpleNodes.get(i - 1);
            
            // Добавляем информацию о предыдущем узле
            currentNode.addNode(previousNode.getNodeInfo());
            previousNode.addNode(currentNode.getNodeInfo());
        }
        
        // Соединяем первый и последний узлы для создания кольца
        if (simpleNodes.size() > 2) {
            SimpleDHTNode firstNode = simpleNodes.get(0);
            SimpleDHTNode lastNode = simpleNodes.get(simpleNodes.size() - 1);
            firstNode.addNode(lastNode.getNodeInfo());
            lastNode.addNode(firstNode.getNodeInfo());
        }
    }
    
    /**
     * Соединение масштабируемых узлов в сеть
     */
    private void connectScalableNodes() {
        for (int i = 1; i < scalableNodes.size(); i++) {
            ScalableDHTNode currentNode = scalableNodes.get(i);
            ScalableDHTNode previousNode = scalableNodes.get(i - 1);
            
            // Добавляем информацию о предыдущем узле
            currentNode.addNode(previousNode.getNodeInfo());
            previousNode.addNode(currentNode.getNodeInfo());
        }
    }
    
    /**
     * Тестирование базовых операций
     */
    private void testBasicOperations() {
        if (simpleNodes.isEmpty()) return;
        
        SimpleDHTNode testNode = simpleNodes.get(0);
        
        // Тест сохранения
        String[] testKeys = {"user:123", "session:abc", "config:main"};
        String[] testValues = {"John Doe", "active_session_data", "main_configuration"};
        
        for (int i = 0; i < testKeys.length; i++) {
            boolean success = testNode.store(testKeys[i], testValues[i]);
            System.out.println(String.format("  %s Сохранение '%s': %s", 
                success ? "✅" : "❌", testKeys[i], success ? "успешно" : "ошибка"));
        }
        
        // Тест поиска
        System.out.println("\nТестирование поиска:");
        for (String key : testKeys) {
            Object result = testNode.find(key);
            System.out.println(String.format("  %s Поиск '%s': %s", 
                result != null ? "✅" : "❌", key, result != null ? "найдено" : "не найдено"));
        }
    }
    
    /**
     * Тестирование продвинутых операций
     */
    private void testAdvancedOperations() {
        if (scalableNodes.isEmpty()) return;
        
        ScalableDHTNode testNode = scalableNodes.get(0);
        
        // Тест с репликацией
        System.out.println("Тестирование репликации данных:");
        String key = "replicated:data";
        String value = "Данные с репликацией";
        
        boolean success = testNode.store(key, value);
        System.out.println(String.format("  %s Сохранение с репликацией: %s", 
            success ? "✅" : "❌", success ? "успешно" : "ошибка"));
        
        // Тест поиска на разных узлах
        System.out.println("\nПоиск реплицированных данных на разных узлах:");
        for (int i = 0; i < scalableNodes.size(); i++) {
            Object result = scalableNodes.get(i).find(key);
            System.out.println(String.format("  %s Узел %d: %s", 
                result != null ? "✅" : "❌", i + 1, result != null ? "найдено" : "не найдено"));
        }
    }
    
    /**
     * Вывод статистики простых узлов
     */
    private void printSimpleNodesStatistics() {
        for (int i = 0; i < simpleNodes.size(); i++) {
            SimpleDHTNode node = simpleNodes.get(i);
            System.out.println(String.format("  Узел %d: Запросов: %d, Успешных: %d, Время работы: %d сек", 
                i + 1, node.getTotalRequests(), node.getSuccessfulRequests(), node.getUptime() / 1000));
        }
    }
    
    /**
     * Вывод статистики масштабируемых узлов
     */
    private void printScalableNodesStatistics() {
        for (int i = 0; i < scalableNodes.size(); i++) {
            ScalableDHTNode node = scalableNodes.get(i);
            System.out.println(String.format("  Масштабируемый узел %d: ID: %s, Порт: %d", 
                i + 1, node.getNodeId().toString().substring(0, 8) + "...", node.getPort()));
        }
    }
    
    /**
     * Очистка ресурсов
     */
    private void cleanup() {
        System.out.println("\n🧹 Очистка ресурсов...");
        
        // Остановка простых узлов
        for (SimpleDHTNode node : simpleNodes) {
            try {
                node.stop();
            } catch (Exception e) {
                logger.warning("Ошибка остановки простого узла: " + e.getMessage());
            }
        }
        
        // Остановка масштабируемых узлов
        for (ScalableDHTNode node : scalableNodes) {
            try {
                node.stop();
            } catch (Exception e) {
                logger.warning("Ошибка остановки масштабируемого узла: " + e.getMessage());
            }
        }
        
        simpleNodes.clear();
        scalableNodes.clear();
        
        System.out.println("✅ Все ресурсы освобождены");
    }
}

