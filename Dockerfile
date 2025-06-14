# Building the JAR
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Running the JAR
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/Connect-0.0.1-SNAPSHOT.jar connect-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "connect-backend.jar"]
