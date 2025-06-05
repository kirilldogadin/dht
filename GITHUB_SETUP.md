# 🚀 GitHub Project Setup Guide

## 📋 Обзор

Этот документ содержит инструкции по настройке GitHub проекта для управления разработкой DHT (Distributed Hash Table).

## ✅ Что уже настроено

### 1. Issues и Milestones
- ✅ Создано 5 Milestones для спринтов
- ✅ Создано 10 Issues для функциональностей (#11-#20)
- ✅ Все Issues привязаны к соответствующим Milestones

### 2. Система меток
- ✅ Типы задач: `feature`, `bug`, `enhancement`, `documentation`, `testing`
- ✅ Приоритеты: `priority: critical/high/medium/low`
- ✅ Спринты: `sprint-1` до `sprint-5`
- ✅ Статусы: `status: todo/in-progress/review/done/blocked`
- ✅ Компоненты: `component: core/network/storage/routing/protocol`

### 3. Шаблоны
- ✅ Feature Request template (`.github/ISSUE_TEMPLATE/feature_request.md`)
- ✅ Bug Report template (`.github/ISSUE_TEMPLATE/bug_report.md`)
- ✅ Pull Request template (`.github/PULL_REQUEST_TEMPLATE/pull_request_template.md`)

### 4. CI/CD
- ✅ GitHub Actions workflow (`.github/workflows/ci.yml`)
- ✅ Тестирование на Java 11 и 17
- ✅ Интеграция с SonarCloud и Codecov

### 5. Документация
- ✅ Development Workflow (`DEVELOPMENT_WORKFLOW.md`)
- ✅ Setup Guide (этот файл)

## 🔧 Дополнительная настройка

### 1. Создание GitHub Project (вручную)

Поскольку API не позволяет создать Project автоматически, создайте его вручную:

1. Перейдите в репозиторий на GitHub
2. Нажмите на вкладку "Projects"
3. Нажмите "New project"
4. Выберите "Board" template
5. Назовите проект "DHT Development Roadmap"
6. Создайте колонки:
   - 📋 **Backlog** (для новых задач)
   - 🔄 **In Progress** (для задач в работе)
   - 👀 **Review** (для задач на ревью)
   - ✅ **Done** (для завершенных задач)

### 2. Добавление Issues в Project

1. В созданном Project нажмите "Add items"
2. Выберите Issues #11-#20
3. Перетащите их в колонку "Backlog"

### 3. Настройка автоматизации Project

1. В Project нажмите на "⚙️" (Settings)
2. Перейдите в "Workflows"
3. Настройте автоматические правила:
   - **Item added to project** → Set status to "Backlog"
   - **Item closed** → Set status to "Done"
   - **Pull request merged** → Set status to "Done"

### 4. Настройка SonarCloud (опционально)

1. Зарегистрируйтесь на [SonarCloud](https://sonarcloud.io/)
2. Импортируйте репозиторий
3. Получите SONAR_TOKEN
4. Добавьте его в GitHub Secrets:
   - Settings → Secrets and variables → Actions
   - New repository secret: `SONAR_TOKEN`

### 5. Настройка Codecov (опционально)

1. Зарегистрируйтесь на [Codecov](https://codecov.io/)
2. Импортируйте репозиторий
3. Получите CODECOV_TOKEN (если репозиторий приватный)
4. Добавьте его в GitHub Secrets: `CODECOV_TOKEN`

## 📊 Использование системы

### Создание новой задачи

1. Перейдите в Issues
2. Нажмите "New issue"
3. Выберите подходящий шаблон
4. Заполните все поля
5. Назначьте метки и Milestone

### Работа с задачей

1. Назначьте себя исполнителем
2. Измените статус на `status: in-progress`
3. Создайте feature ветку
4. Разрабатывайте согласно workflow
5. Создайте Pull Request

### Отслеживание прогресса

1. **Issues** - для детального управления задачами
2. **Milestones** - для отслеживания прогресса спринтов
3. **Project Board** - для визуализации workflow
4. **Actions** - для мониторинга CI/CD

## 📈 Метрики и отчетность

### Доступные метрики

1. **Velocity** - количество завершенных Issues за спринт
2. **Burndown** - прогресс выполнения Milestone
3. **Code Coverage** - покрытие тестами (через Codecov)
4. **Code Quality** - качество кода (через SonarCloud)

### Еженедельные отчеты

Рекомендуется создавать еженедельные отчеты о прогрессе:

1. Количество закрытых Issues
2. Прогресс по текущему Milestone
3. Проблемы и блокеры
4. Планы на следующую неделю

## 🔗 Полезные ссылки

- [GitHub Issues](https://github.com/kirilldogadin/dht/issues)
- [GitHub Milestones](https://github.com/kirilldogadin/dht/milestones)
- [GitHub Actions](https://github.com/kirilldogadin/dht/actions)
- [Development Workflow](./DEVELOPMENT_WORKFLOW.md)

## 🆘 Поддержка

Если возникают вопросы по настройке или использованию:

1. Создайте Issue с меткой `documentation`
2. Опишите проблему подробно
3. Приложите скриншоты (если необходимо)

---

*Этот документ обновляется по мере развития проекта и добавления новых инструментов.*

