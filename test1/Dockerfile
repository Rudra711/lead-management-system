# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

# ✅ move into correct folder
WORKDIR /app/test1/test1

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk

WORKDIR /app

# ✅ correct path to jar
COPY --from=build /app/test1/test1/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]