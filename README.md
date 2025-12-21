# ShareIt

Учебный проект для сервиса совместного использования вещей: пользователи публикуют вещи, создают запросы на нужные предметы и бронируют доступные варианты.

## Архитектура

Проект состоит из двух модулей:

- `gateway` — HTTP-шлюз и валидация входящих запросов.
- `server` — основная бизнес-логика и работа с базой данных.

## Функциональность

- управление пользователями;
- управление вещами и комментариями;
- бронирование вещей и подтверждение бронирований владельцем;
- запросы на вещи и подбор ответов.

## Технологии

- Java 21
- Spring Boot 3.3
- Maven
- PostgreSQL 16
- Docker / Docker Compose
- Lombok

## Быстрый старт (Docker)

1. Соберите и запустите сервисы:
   ```bash
   docker compose up --build
   ```
2. Доступные порты:
   - Gateway: `http://localhost:8080`
   - Server: `http://localhost:9090`
   - PostgreSQL: `localhost:6541`

## Локальный запуск без Docker Compose

1. Поднимите базу данных PostgreSQL:
   ```bash
   docker run --name shareit-db \
     -e POSTGRES_DB=shareit \
     -e POSTGRES_USER=shareit \
     -e POSTGRES_PASSWORD=shareit \
     -p 6541:5432 \
     -d postgres:16.1
   ```
2. Запустите `server`:
   ```bash
   mvn -pl server -am spring-boot:run
   ```
3. Запустите `gateway`:
   ```bash
   mvn -pl gateway -am spring-boot:run
   ```

## Конфигурация

Основные параметры:

- `gateway/src/main/resources/application.properties`
  - `server.port=8080`
  - `shareit-server.url=http://localhost:9090`
- `server/src/main/resources/application.properties`
  - `server.port=9090`
  - `spring.datasource.url=jdbc:postgresql://localhost:6541/shareit`
  - `spring.datasource.username=shareit`
  - `spring.datasource.password=shareit`

## API

Все запросы, требующие идентификации пользователя, ожидают заголовок:

```
X-Sharer-User-Id: <userId>
```

Коллекция Postman для ручного тестирования: `postman/sprint.json`.

## Тестирование

```bash
mvn test
```
