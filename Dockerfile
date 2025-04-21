FROM openjdk:17-jdk-slim
WORKDIR /app

# Копируем проект
COPY . .

# Сборка (если Gradle)
RUN ./gradlew build

# Запуск
CMD ["java", "-jar", "build/libs/0.0.1-SNAPSHOT.jar"]