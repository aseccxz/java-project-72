### Hexlet tests and linter status:
[![Actions Status](https://github.com/aseccxz/java-project-72/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/aseccxz/java-project-72/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=aseccxz_java-project-72&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=aseccxz_java-project-72)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=aseccxz_java-project-72&metric=coverage)](https://sonarcloud.io/summary/new_code?id=aseccxz_java-project-72)
# Анализатор страниц
Веб-приложение для проверки доступности сайтов и базового SEO-анализа веб-страниц.

Пользователь может добавить URL сайта, запустить его проверку и получить HTTP-статус страницы, а также основные SEO-параметры: title, h1 и meta description. Результаты проверок сохраняются в базе данных и доступны для последующего просмотра.

Основные возможности
добавление и нормализация URL;
проверка доступности веб-страниц по HTTP;
получение HTTP-кода ответа;
парсинг HTML и извлечение title, h1 и meta description;
хранение сайтов и истории проверок в базе данных;
серверный рендеринг HTML-страниц;
автоматическое тестирование приложения.

Стек

Backend: Java, Javalin
Frontend: JTE, Bootstrap
Database: PostgreSQL, H2, HikariCP
HTTP & Parsing: Unirest, Jsoup
Testing: JUnit, MockWebServer, AssertJ
Build & Quality: Gradle, GitHub Actions, Checkstyle, JaCoCo, SonarQube

Демо

Приложение развёрнуто и доступно онлайн: 
[Ссылка на сайт](https://url-checker-5579.onrender.com)  
