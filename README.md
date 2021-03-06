#Shop Price Service

Сервис предназначен для расчета оригинальной стоимости украшений.

##Функциональные требования:
1. Загрузка html страничек заказов и обогащение БД материалами
2. Загрузка украшений с основного сайта и отображение информации о них
3. Фильтрация и поиск необходимых материалов по магазину, заказу и названию
4. Формирование списка материалов для каждого украшения и расчет оригинальной стоимости
5. Сохранение в БД сформированного списка материалов украшений
6. Возможность аутентификации пользователей

##Нефункциональные требования:
1. Быстрый отклик приложения: данные кешируются, выгрузка из БД и запрос в другие сервисы происходят по мере необходимости
2. Удобная сборка и раскатка приложения: происходит через docker compose вместе с БД
3. Тесты на бизнес логику и слой DAO

##Используемые технологии:
1. Java 17
2. Spring Boot Web/Data JPA/Security/Test
3. JUnit5, Mockito, TestContainers
4. Lombok
5. Flyway, PostgreSQL
6. Cache Guava
7. Docker

##Сборка и запуск:
1. ./gradlew clean build -x test
2. run ShopPriceApplication.java

##Тесты:
1. ./gradlew test

##Через докер:
1. Проверить application.yml/scripts.js:

`spring.datasource.url = jdbc:postgresql://host.docker.internal:5430/shopPriceDB`

`let address = "http://185.46.8.125:8080"`

2. gradle clean build
3. docker login -u alena1112
4. sh docker-build.sh build latest
5. sh docker-build.sh push latest
6. docker-compose up -d --build
7. Остановить и удалить контейнеры: docker-compose stop/down

##Посмотреть логи: 
1. docker exec -ti shop-price_shop-price-service_1 /bin/sh
2. tail -f shop_price.log
3. Или docker container logs shop-price_shop-price-service_1

##Будущие фичи:
1. Кеш через redis
2. **Блок с рекомендательным подбором материалов для украшений: построение рекомендаций по типу украшения, описанию, поиск по материалам**
