# 🔥 1. Первый этап: Скачивание зависимостей (для кэша)
FROM maven:3.9.6-eclipse-temurin-21 AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# 🏗 2. Второй этап: Сборка приложения (использует кэш)
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY . .
RUN mvn clean package -DskipTests

# 🚀 3. Финальный контейнер (на основе JDK)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 🚀 Запуск приложения
CMD ["java", "-jar", "app.jar"]