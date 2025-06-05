package global.unet.reports;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер отчетов о статусе проектов.
 * Предоставляет методы для работы с отчетами, включая HTML отчеты.
 */
public class ReportManager {
    
    private static final String REPORTS_DIR = "reports";
    
    /**
     * Получает список всех доступных отчетов.
     * 
     * @return список имен файлов отчетов
     */
    public List<String> getAvailableReports() {
        List<String> reports = new ArrayList<>();
        
        try {
            // Получаем список ресурсов в директории отчетов
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(REPORTS_DIR);
            
            if (inputStream != null) {
                File reportsDir = new File(classLoader.getResource(REPORTS_DIR).getFile());
                File[] files = reportsDir.listFiles();
                
                if (files != null) {
                    for (File file : files) {
                        reports.add(file.getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при получении списка отчетов: " + e.getMessage());
        }
        
        return reports;
    }
    
    /**
     * Получает содержимое HTML отчета по имени файла.
     * 
     * @param reportName имя файла отчета
     * @return содержимое отчета в виде строки или null, если отчет не найден
     */
    public String getHtmlReport(String reportName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(REPORTS_DIR + "/" + reportName);
            
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении отчета: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Сохраняет HTML отчет в директорию ресурсов.
     * 
     * @param reportName имя файла отчета
     * @param content содержимое отчета
     * @return true, если отчет успешно сохранен, иначе false
     */
    public boolean saveHtmlReport(String reportName, String content) {
        try {
            // Получаем путь к директории ресурсов
            String resourcesPath = getClass().getClassLoader().getResource("").getPath();
            Path reportsPath = Paths.get(resourcesPath, REPORTS_DIR);
            
            // Создаем директорию, если она не существует
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
            }
            
            // Сохраняем отчет
            Path reportPath = reportsPath.resolve(reportName);
            Files.writeString(reportPath, content);
            
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении отчета: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Экспортирует HTML отчет в указанную директорию.
     * 
     * @param reportName имя файла отчета
     * @param exportPath путь для экспорта
     * @return true, если отчет успешно экспортирован, иначе false
     */
    public boolean exportHtmlReport(String reportName, String exportPath) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(REPORTS_DIR + "/" + reportName);
            
            if (inputStream != null) {
                Path targetPath = Paths.get(exportPath, reportName);
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при экспорте отчета: " + e.getMessage());
        }
        
        return false;
    }
}

