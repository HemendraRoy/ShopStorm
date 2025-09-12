## Stage 1: Build the JAR
#FROM maven:3.9.3 AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -DskipTests
#
## Stage 2: Run the JAR
#FROM openjdk:21
#WORKDIR /app
#COPY --from=build /app/target/ShopStorm-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
COPY --from=build /target/ShopStorm-0.0.1-SNAPSHOT.jar ShopStorm.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","ShopStorm.jar"]