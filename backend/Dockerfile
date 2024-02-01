#
# Build stage
#
# FROM maven:3.8.3-openjdk-17 AS build
# COPY src /backend/src
# COPY pom.xml /backend
# RUN mvn -f /backend/pom.xml clean package -DskipTests

#
# Run stage
#
FROM openjdk:17
COPY target/bank-0.0.1-SNAPSHOT.jar bank.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/bank.jar"]
