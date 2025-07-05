````markdown
# Hotel API

RESTful API для работы с отелями.

## Содержание

- [Технологии](#технологии)
- [Функционал](#функционал)
- [Запуск приложения](#запуск-приложения)  
  - [По умолчанию (H2)](#по-умолчанию-h2)  
  - [Профиль MySQL](#профиль-mysql)  
- [Переменные окружения](#переменные-окружения)
- [Swagger / OpenAPI](#swagger--openapi)
- [Endpoints](#endpoints)
- [Тесты](#тесты)

---

## Технологии

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Liquibase
- H2 (in-memory) / MySQL (опционально)
- Swagger (springdoc-openapi)

---

## Функционал

1. **GET** `/property-view/hotels`  
   Краткий список всех отелей.  
2. **GET** `/property-view/hotels/{id}`  
   Полная информация по конкретному отелю.  
3. **GET** `/property-view/search`  
   Поиск по параметрам: `name`, `brand`, `city`, `country`, `amenities`.  
4. **POST** `/property-view/hotels`  
   Создание нового отеля.  
5. **POST** `/property-view/hotels/{id}/amenities`  
   Добавление списка удобств к существующему отелю.  
6. **GET** `/property-view/histogram/{param}`  
   Группировка по `brand`, `city`, `country` или `amenities`.  

> **Порт:** 8092  
> **URL префикс:** `/property-view`

---

## Запуск приложения

### По умолчанию (H2)

```bash
mvn clean spring-boot:run
````

* **База:** H2 in-memory
* **URL:** `jdbc:h2:mem:hoteldb`
* **Пользователь:** `sa`
* **Пароль:** `Nikitikk1315`

Все изменения схемы и загрузка тестовых данных выполняются Liquibase (файлы в `src/main/resources/db/changelog`).

---

### Профиль MySQL

```bash
mvn clean spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Настройки в `application-mysql.yml` (пример):

```yaml
spring:
  config:
    activate:
      on-profile: mysql

  datasource:
    url: jdbc:mysql://localhost:3306/hoteldb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: nikitikk
    password: Nikitikk1315
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

---

## Swagger / OpenAPI

После запуска доступно:

* Swagger UI: `http://localhost:8092/swagger-ui.html`
* Альтернативный URL: `http://localhost:8092/swagger-ui/index.html`

Там можно посмотреть описание всех контроллеров, моделей и попробовать запросы прямо из браузера.

---

## Endpoints

| Метод | Путь                                   | Описание                                                              |
| ----- | -------------------------------------- | --------------------------------------------------------------------- |
| GET   | `/property-view/hotels`                | Список всех отелей (краткая информация)                               |
| GET   | `/property-view/hotels/{id}`           | Детальная информация по отелю                                         |
| GET   | `/property-view/search[?params]`       | Поиск по параметрам (`name`, `brand`, `city`, `country`, `amenities`) |
| POST  | `/property-view/hotels`                | Создать новый отель                                                   |
| POST  | `/property-view/hotels/{id}/amenities` | Добавить список удобств к отелю                                       |
| GET   | `/property-view/histogram/{param}`     | Гистограмма по параметру (`brand`, `city`, `country`, `amenities`)    |

---

## Тесты

* **Unit-tests:** `HotelServiceTest`, `HotelControllerTest`
* **Интеграционные:** `HotelApiIntegrationTest`

Запустить:

```bash
mvn test
```

---

> Если что-то пойдёт не так в CI, убедитесь, что параметры подключения к БД совпадают с дефолтными (H2) или переданы через профиль / переменные окружения.

---

**Автор:** Nikitikk
**Email:** [miracleqxz@gmail.com](mailto:miracleqxz@gmail.com)
**Версия:** 1.0.0
