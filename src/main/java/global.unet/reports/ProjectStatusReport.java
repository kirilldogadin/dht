package global.unet.reports;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Представляет отчет о статусе проекта.
 * Содержит информацию о состоянии проекта, прогрессе, проблемах и рекомендациях.
 */
public class ProjectStatusReport {
    
    private String projectName;
    private String projectDescription;
    private LocalDateTime generationDate;
    private int progressPercentage;
    private List<Component> components;
    private List<Issue> issues;
    private List<String> recommendations;
    private Map<String, String> additionalInfo;
    
    /**
     * Создает новый отчет о статусе проекта.
     * 
     * @param projectName название проекта
     * @param projectDescription описание проекта
     */
    public ProjectStatusReport(String projectName, String projectDescription) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.generationDate = LocalDateTime.now();
        this.progressPercentage = 0;
        this.components = new ArrayList<>();
        this.issues = new ArrayList<>();
        this.recommendations = new ArrayList<>();
        this.additionalInfo = new HashMap<>();
    }
    
    /**
     * Добавляет компонент проекта в отчет.
     * 
     * @param name название компонента
     * @param status статус компонента
     * @param description описание компонента
     * @param completionPercentage процент завершения компонента
     * @return текущий отчет для цепочки вызовов
     */
    public ProjectStatusReport addComponent(String name, ComponentStatus status, String description, int completionPercentage) {
        components.add(new Component(name, status, description, completionPercentage));
        return this;
    }
    
    /**
     * Добавляет проблему в отчет.
     * 
     * @param title заголовок проблемы
     * @param description описание проблемы
     * @param severity серьезность проблемы
     * @return текущий отчет для цепочки вызовов
     */
    public ProjectStatusReport addIssue(String title, String description, IssueSeverity severity) {
        issues.add(new Issue(title, description, severity));
        return this;
    }
    
    /**
     * Добавляет рекомендацию в отчет.
     * 
     * @param recommendation текст рекомендации
     * @return текущий отчет для цепочки вызовов
     */
    public ProjectStatusReport addRecommendation(String recommendation) {
        recommendations.add(recommendation);
        return this;
    }
    
    /**
     * Добавляет дополнительную информацию в отчет.
     * 
     * @param key ключ информации
     * @param value значение информации
     * @return текущий отчет для цепочки вызовов
     */
    public ProjectStatusReport addAdditionalInfo(String key, String value) {
        additionalInfo.put(key, value);
        return this;
    }
    
    /**
     * Устанавливает общий процент завершения проекта.
     * 
     * @param progressPercentage процент завершения проекта
     * @return текущий отчет для цепочки вызовов
     */
    public ProjectStatusReport setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
        return this;
    }
    
    // Геттеры
    
    public String getProjectName() {
        return projectName;
    }
    
    public String getProjectDescription() {
        return projectDescription;
    }
    
    public LocalDateTime getGenerationDate() {
        return generationDate;
    }
    
    public int getProgressPercentage() {
        return progressPercentage;
    }
    
    public List<Component> getComponents() {
        return components;
    }
    
    public List<Issue> getIssues() {
        return issues;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
    
    /**
     * Представляет компонент проекта в отчете.
     */
    public static class Component {
        private String name;
        private ComponentStatus status;
        private String description;
        private int completionPercentage;
        
        public Component(String name, ComponentStatus status, String description, int completionPercentage) {
            this.name = name;
            this.status = status;
            this.description = description;
            this.completionPercentage = completionPercentage;
        }
        
        public String getName() {
            return name;
        }
        
        public ComponentStatus getStatus() {
            return status;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getCompletionPercentage() {
            return completionPercentage;
        }
    }
    
    /**
     * Представляет проблему в отчете.
     */
    public static class Issue {
        private String title;
        private String description;
        private IssueSeverity severity;
        
        public Issue(String title, String description, IssueSeverity severity) {
            this.title = title;
            this.description = description;
            this.severity = severity;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public IssueSeverity getSeverity() {
            return severity;
        }
    }
    
    /**
     * Перечисление возможных статусов компонента.
     */
    public enum ComponentStatus {
        NOT_STARTED("Не начат"),
        IN_PROGRESS("В процессе"),
        COMPLETED("Завершен"),
        BLOCKED("Заблокирован"),
        NEEDS_REVIEW("Требует проверки");
        
        private final String displayName;
        
        ComponentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Перечисление возможных уровней серьезности проблемы.
     */
    public enum IssueSeverity {
        LOW("Низкая"),
        MEDIUM("Средняя"),
        HIGH("Высокая"),
        CRITICAL("Критическая");
        
        private final String displayName;
        
        IssueSeverity(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

