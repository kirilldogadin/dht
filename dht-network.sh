#!/bin/bash

# DHT Network Management Script
# Управление DHT сетью из 6 узлов с мониторингом

set -e

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Функции для цветного вывода
print_header() {
    echo -e "${PURPLE}================================${NC}"
    echo -e "${PURPLE}$1${NC}"
    echo -e "${PURPLE}================================${NC}"
}

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Проверка зависимостей
check_dependencies() {
    print_status "Проверка зависимостей..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker не установлен. Установите Docker и повторите попытку."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose не установлен. Установите Docker Compose и повторите попытку."
        exit 1
    fi
    
    print_success "Все зависимости установлены"
}

# Сборка проекта
build_project() {
    print_status "Сборка Java проекта..."
    
    if [ -f "./gradlew" ]; then
        ./gradlew build -x test
        print_success "Java проект собран"
    else
        print_warning "Gradle wrapper не найден, пропускаем сборку Java"
    fi
}

# Запуск DHT сети
start_network() {
    print_header "🚀 ЗАПУСК DHT СЕТИ"
    
    check_dependencies
    build_project
    
    print_status "Запуск DHT узлов..."
    docker-compose up -d dht-bootstrap dht-node-2 dht-node-3 dht-node-4 dht-node-5 dht-node-6 dht-monitor
    
    print_status "Ожидание запуска узлов..."
    sleep 10
    
    print_success "DHT сеть запущена!"
    print_status "Доступные сервисы:"
    echo "  🌐 Веб-дашборд: http://localhost:3000"
    echo "  🔧 DHT узлы: http://localhost:8080-8085"
    echo "  📊 API документация: http://localhost:8080/api/docs"
}

# Запуск с мониторингом
start_with_monitoring() {
    print_header "🚀 ЗАПУСК DHT СЕТИ С МОНИТОРИНГОМ"
    
    check_dependencies
    build_project
    
    print_status "Запуск DHT узлов с мониторингом..."
    docker-compose --profile monitoring up -d
    
    print_status "Ожидание запуска всех сервисов..."
    sleep 15
    
    print_success "DHT сеть с мониторингом запущена!"
    print_status "Доступные сервисы:"
    echo "  🌐 Веб-дашборд: http://localhost:3000"
    echo "  🔧 DHT узлы: http://localhost:8080-8085"
    echo "  📊 Prometheus: http://localhost:9090"
    echo "  📈 Grafana: http://localhost:3001 (admin/admin)"
}

# Остановка сети
stop_network() {
    print_header "🛑 ОСТАНОВКА DHT СЕТИ"
    
    print_status "Остановка всех контейнеров..."
    docker-compose down
    
    print_success "DHT сеть остановлена"
}

# Проверка статуса
check_status() {
    print_header "📊 СТАТУС DHT СЕТИ"
    
    print_status "Статус контейнеров:"
    docker-compose ps
    
    echo ""
    print_status "Проверка здоровья узлов:"
    
    nodes=("8080" "8081" "8082" "8083" "8084" "8085")
    healthy_nodes=0
    
    for port in "${nodes[@]}"; do
        if curl -s -f "http://localhost:$port/api/health" > /dev/null 2>&1; then
            echo -e "  ✅ Узел на порту $port: ${GREEN}ЗДОРОВ${NC}"
            ((healthy_nodes++))
        else
            echo -e "  ❌ Узел на порту $port: ${RED}НЕДОСТУПЕН${NC}"
        fi
    done
    
    echo ""
    if [ $healthy_nodes -eq 6 ]; then
        print_success "Статус сети: $healthy_nodes/6 узлов здоровы - ВСЯ СЕТЬ РАБОТАЕТ ОТЛИЧНО! 🎉"
    elif [ $healthy_nodes -ge 3 ]; then
        print_warning "Статус сети: $healthy_nodes/6 узлов здоровы - сеть работает с ограничениями"
    else
        print_error "Статус сети: $healthy_nodes/6 узлов здоровы - критическое состояние!"
    fi
}

# Тестирование сети
test_network() {
    print_header "🧪 ТЕСТИРОВАНИЕ DHT СЕТИ"
    
    print_status "Тестирование базовых операций..."
    
    # Тест сохранения данных
    print_status "Тест 1: Сохранение данных..."
    if curl -s -X PUT "http://localhost:8080/api/store/test-key" \
        -H "Content-Type: application/json" \
        -d '{"value": "test-value", "timestamp": "'$(date -Iseconds)'"}' > /dev/null; then
        print_success "✅ Данные сохранены"
    else
        print_error "❌ Ошибка сохранения данных"
        return 1
    fi
    
    sleep 2
    
    # Тест поиска данных
    print_status "Тест 2: Поиск данных на другом узле..."
    if result=$(curl -s "http://localhost:8081/api/find/test-key"); then
        if echo "$result" | grep -q "test-value"; then
            print_success "✅ Данные найдены на другом узле"
        else
            print_warning "⚠️ Данные не найдены (возможно, еще не реплицированы)"
        fi
    else
        print_error "❌ Ошибка поиска данных"
        return 1
    fi
    
    # Тест репликации
    print_status "Тест 3: Проверка репликации..."
    found_count=0
    for port in 8080 8081 8082 8083 8084 8085; do
        if curl -s "http://localhost:$port/api/find/test-key" | grep -q "test-value"; then
            ((found_count++))
        fi
    done
    
    if [ $found_count -ge 3 ]; then
        print_success "✅ Тест репликации прошел успешно! Данные найдены на $found_count узлах"
    else
        print_warning "⚠️ Репликация неполная: данные найдены только на $found_count узлах"
    fi
    
    print_success "🎉 Тестирование завершено!"
}

# Мониторинг в реальном времени
monitor_network() {
    print_header "📊 МОНИТОРИНГ DHT СЕТИ"
    
    print_status "Запуск мониторинга в реальном времени..."
    print_status "Нажмите Ctrl+C для выхода"
    
    while true; do
        clear
        echo -e "${PURPLE}DHT Network Monitor - $(date)${NC}"
        echo "================================"
        
        # Статус узлов
        echo -e "${CYAN}Статус узлов:${NC}"
        for port in 8080 8081 8082 8083 8084 8085; do
            if curl -s -f "http://localhost:$port/api/health" > /dev/null 2>&1; then
                echo -e "  ✅ Узел $port: ${GREEN}ОНЛАЙН${NC}"
            else
                echo -e "  ❌ Узел $port: ${RED}ОФЛАЙН${NC}"
            fi
        done
        
        echo ""
        echo -e "${CYAN}Статистика сети:${NC}"
        
        # Получаем статистику с первого доступного узла
        for port in 8080 8081 8082 8083 8084 8085; do
            if stats=$(curl -s "http://localhost:$port/api/stats" 2>/dev/null); then
                echo "  📊 Статистика с узла $port:"
                echo "$stats" | jq -r '
                    "    Всего ключей: " + (.totalKeys // "N/A" | tostring) + 
                    "\n    Активных соединений: " + (.activeConnections // "N/A" | tostring) +
                    "\n    Время работы: " + (.uptime // "N/A" | tostring)
                ' 2>/dev/null || echo "    Статистика недоступна"
                break
            fi
        done
        
        echo ""
        echo -e "${CYAN}Ресурсы Docker:${NC}"
        docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}" | grep dht
        
        sleep 5
    done
}

# Просмотр логов
view_logs() {
    print_header "📋 ЛОГИ DHT СЕТИ"
    
    if [ -n "$1" ]; then
        print_status "Просмотр логов контейнера: $1"
        docker-compose logs -f "$1"
    else
        print_status "Просмотр логов всех DHT узлов:"
        docker-compose logs -f dht-bootstrap dht-node-2 dht-node-3 dht-node-4 dht-node-5 dht-node-6
    fi
}

# Очистка системы
cleanup() {
    print_header "🧹 ОЧИСТКА СИСТЕМЫ"
    
    print_status "Остановка и удаление контейнеров..."
    docker-compose down -v
    
    print_status "Удаление образов DHT..."
    docker rmi $(docker images | grep dht | awk '{print $3}') 2>/dev/null || true
    
    print_status "Очистка неиспользуемых ресурсов Docker..."
    docker system prune -f
    
    print_success "Система очищена"
}

# Справка
show_help() {
    print_header "📖 СПРАВКА ПО УПРАВЛЕНИЮ DHT СЕТЬЮ"
    
    echo "Использование: $0 [команда]"
    echo ""
    echo "Доступные команды:"
    echo "  start              Запустить DHT сеть (6 узлов + веб-дашборд)"
    echo "  start-monitoring   Запустить с полным мониторингом (+ Prometheus + Grafana)"
    echo "  stop               Остановить DHT сеть"
    echo "  status             Проверить статус сети"
    echo "  test               Протестировать работу сети"
    echo "  monitor            Мониторинг в реальном времени"
    echo "  logs [container]   Просмотр логов (всех или конкретного контейнера)"
    echo "  cleanup            Полная очистка системы"
    echo "  help               Показать эту справку"
    echo ""
    echo "Примеры:"
    echo "  $0 start                    # Запустить базовую сеть"
    echo "  $0 start-monitoring         # Запустить с мониторингом"
    echo "  $0 logs dht-bootstrap       # Логи bootstrap узла"
    echo "  $0 test                     # Протестировать сеть"
    echo ""
    echo "Веб-интерфейсы:"
    echo "  🌐 Дашборд:     http://localhost:3000"
    echo "  📊 Prometheus:  http://localhost:9090"
    echo "  📈 Grafana:     http://localhost:3001 (admin/admin)"
    echo "  🔧 DHT API:     http://localhost:8080-8085"
}

# Основная логика
case "${1:-help}" in
    "start")
        start_network
        ;;
    "start-monitoring")
        start_with_monitoring
        ;;
    "stop")
        stop_network
        ;;
    "status")
        check_status
        ;;
    "test")
        test_network
        ;;
    "monitor")
        monitor_network
        ;;
    "logs")
        view_logs "$2"
        ;;
    "cleanup")
        cleanup
        ;;
    "help"|*)
        show_help
        ;;
esac

