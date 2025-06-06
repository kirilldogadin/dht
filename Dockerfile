# Dockerfile для DHT узла
FROM openjdk:17-jdk-slim

# Установка необходимых пакетов
RUN apt-get update && apt-get install -y \
    curl \
    netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*

# Создание рабочей директории
WORKDIR /app

# Копирование JAR файла
COPY build/libs/*.jar app.jar

# Создание директории для данных
RUN mkdir -p /app/data

# Порт для DHT узла
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

