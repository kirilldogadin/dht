package global.unet.reports.html;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Генератор HTML отчетов о статусе проектов.
 * Предоставляет методы для создания и настройки HTML отчетов.
 */
public class HtmlReportGenerator {
    
    private static final String REPORTS_DIR = "reports";
    private static final String TEMPLATE_NAME = "report_template.html";
    
    private String title;
    private String description;
    private Map<String, String> sections;
    
    /**
     * Создает новый генератор HTML отчетов с указанным заголовком и описанием.
     * 
     * @param title заголовок отчета
     * @param description описание отчета
     */
    public HtmlReportGenerator(String title, String description) {
        this.title = title;
        this.description = description;
        this.sections = new HashMap<>();
    }
    
    /**
     * Добавляет секцию в отчет.
     * 
     * @param sectionId идентификатор секции
     * @param content содержимое секции в формате HTML
     * @return текущий генератор для цепочки вызовов
     */
    public HtmlReportGenerator addSection(String sectionId, String content) {
        sections.put(sectionId, content);
        return this;
    }
    
    /**
     * Генерирует HTML отчет на основе шаблона и добавленных секций.
     * 
     * @return содержимое HTML отчета
     * @throws IOException если произошла ошибка при чтении шаблона
     */
    public String generate() throws IOException {
        // Загружаем шаблон отчета
        String template = loadTemplate();
        
        if (template == null) {
            throw new IOException("Не удалось загрузить шаблон отчета");
        }
        
        // Заменяем плейсхолдеры в шаблоне
        String report = template
                .replace("{{TITLE}}", title)
                .replace("{{DESCRIPTION}}", description)
                .replace("{{GENERATION_DATE}}", getCurrentDateTime());
        
        // Добавляем содержимое секций
        for (Map.Entry<String, String> entry : sections.entrySet()) {
            report = report.replace("{{SECTION_" + entry.getKey() + "}}", entry.getValue());
        }
        
        return report;
    }
    
    /**
     * Сохраняет сгенерированный HTML отчет в файл.
     * 
     * @param fileName имя файла для сохранения
     * @return путь к сохраненному файлу
     * @throws IOException если произошла ошибка при сохранении
     */
    public String saveToFile(String fileName) throws IOException {
        String report = generate();
        
        // Получаем путь к директории ресурсов
        String resourcesPath = getClass().getClassLoader().getResource("").getPath();
        Path reportsPath = Paths.get(resourcesPath, REPORTS_DIR);
        
        // Создаем директорию, если она не существует
        if (!Files.exists(reportsPath)) {
            Files.createDirectories(reportsPath);
        }
        
        // Сохраняем отчет
        Path reportPath = reportsPath.resolve(fileName);
        Files.writeString(reportPath, report);
        
        return reportPath.toString();
    }
    
    /**
     * Загружает шаблон HTML отчета из ресурсов.
     * 
     * @return содержимое шаблона или null, если шаблон не найден
     */
    private String loadTemplate() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(REPORTS_DIR + "/" + TEMPLATE_NAME);
            
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении шаблона: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Возвращает текущую дату и время в отформатированном виде.
     * 
     * @return строка с текущей датой и временем
     */
    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return now.format(formatter);
    }
}

