package global.unet.reports.github;

import global.unet.reports.ProjectStatusReport;
import global.unet.reports.ProjectStatusReport.ComponentStatus;
import global.unet.reports.ProjectStatusReport.IssueSeverity;

import java.io.IOException;
import java.util.List;

/**
 * Пример интеграции с GitHub для управления задачами проекта.
 * Демонстрирует, как можно использовать GitHubTaskManager для автоматизации работы с задачами.
 */
public class GitHubIntegrationExample {
    
    /**
     * Основной метод для демонстрации интеграции с GitHub.
     * 
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // Параметры для подключения к GitHub
        String repositoryOwner = "kirilldogadin";
        String repositoryName = "dht";
        String accessToken = "your_access_token_here"; // В реальном коде токен должен храниться в безопасном месте
        
        try {
            // Создаем менеджер задач GitHub
            GitHubTaskManager taskManager = new GitHubTaskManager(repositoryOwner, repositoryName, accessToken);
            
            // Получаем список открытых задач
            System.out.println("Получение списка открытых задач...");
            List<GitHubTaskManager.GitHubTask> openTasks = taskManager.getOpenTasks();
            System.out.println("Открытые задачи:");
            for (GitHubTaskManager.GitHubTask task : openTasks) {
                System.out.println(" - " + task);
            }
            
            // Создаем новую задачу
            System.out.println("\nСоздание новой задачи...");
            GitHubTaskManager.GitHubTask newTask = taskManager.createTask(
                    "Реализовать итеративный алгоритм поиска",
                    "Необходимо реализовать итеративный алгоритм поиска для Kademlia",
                    "feature", "high"
            );
            System.out.println("Создана новая задача: " + newTask);
            
            // Обновляем статус задачи
            System.out.println("\nОбновление статуса задачи...");
            taskManager.updateTaskStatus(newTask.getId(), "in-progress");
            
            // Создаем отчет о статусе проекта на основе задач из GitHub
            System.out.println("\nСоздание отчета о статусе проекта...");
            ProjectStatusReport report = createReportFromGitHubTasks(openTasks);
            
            // Выводим информацию из отчета
            System.out.println("Отчет о статусе проекта:");
            System.out.println("Проект: " + report.getProjectName());
            System.out.println("Прогресс: " + report.getProgressPercentage() + "%");
            System.out.println("Компоненты:");
            for (ProjectStatusReport.Component component : report.getComponents()) {
                System.out.println(" - " + component.getName() + ": " + component.getStatus().getDisplayName() + 
                        " (" + component.getCompletionPercentage() + "%)");
            }
            
            // Закрываем задачу
            System.out.println("\nЗакрытие задачи...");
            taskManager.closeTask(1, "Задача выполнена. XOR метрика реализована.");
            
        } catch (IOException e) {
            System.err.println("Ошибка при работе с GitHub API: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Создает отчет о статусе проекта на основе задач из GitHub.
     * 
     * @param tasks список задач из GitHub
     * @return отчет о статусе проекта
     */
    private static ProjectStatusReport createReportFromGitHubTasks(List<GitHubTaskManager.GitHubTask> tasks) {
        ProjectStatusReport report = new ProjectStatusReport("DHT", "Distributed Hash Table");
        
        // Устанавливаем общий прогресс проекта
        report.setProgressPercentage(35);
        
        // Добавляем компоненты на основе задач
        for (GitHubTaskManager.GitHubTask task : tasks) {
            ComponentStatus status = ComponentStatus.NOT_STARTED;
            int completionPercentage = 0;
            
            // Определяем статус и процент выполнения на основе типа задачи
            if (task.getTitle().contains("XOR метрика")) {
                status = ComponentStatus.IN_PROGRESS;
                completionPercentage = 70;
            } else if (task.getTitle().contains("k-buckets")) {
                status = ComponentStatus.NOT_STARTED;
                completionPercentage = 0;
            }
            
            // Добавляем компонент в отчет
            report.addComponent(task.getTitle(), status, task.getDescription(), completionPercentage);
            
            // Добавляем проблему, если приоритет высокий
            if (task.getPriority().equals("high")) {
                report.addIssue(task.getTitle(), task.getDescription(), IssueSeverity.HIGH);
            }
        }
        
        // Добавляем рекомендации
        report.addRecommendation("Начать с реализации XOR метрики");
        report.addRecommendation("Затем реализовать k-buckets с LRU логикой");
        
        return report;
    }
}

