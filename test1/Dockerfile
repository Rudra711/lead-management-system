# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build


WORKDIR /app

# Copy only backend folder
COPY test1 ./test1

# Move into correct directory
WORKDIR /app/test1

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/test1/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]