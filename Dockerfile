# FROM maven:3.9.6-eclipse-temurin-21 AS build
# COPY . .
# RUN mvn clean package -DskipTests

# FROM openjdk:21-jdk-slim
# COPY --from=build /target/ShopStorm-0.0.1-SNAPSHOT.jar ShopStorm.jar
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","ShopStorm.jar"]

FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app
COPY --from=build /app/target/ShopStorm-0.0.1-SNAPSHOT.jar ShopStorm.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","ShopStorm.jar"]
