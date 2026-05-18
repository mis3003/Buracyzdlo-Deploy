# Etap 1: Budowanie aplikacji
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etap 2: Uruchamianie aplikacji (chudy obraz)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Kopiujemy tylko sam plik JAR z poprzedniego etapu
COPY --from=build /app/target/*.jar app.jar

# Definiujemy port
EXPOSE 8080

# Uruchomienie
ENTRYPOINT ["java", "-jar", "app.jar"]