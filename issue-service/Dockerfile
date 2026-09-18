# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copier uniquement le pom.xml d'abord pour profiter du cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le reste du code source et builder
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copier le jar buildé depuis le stage précédent
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]