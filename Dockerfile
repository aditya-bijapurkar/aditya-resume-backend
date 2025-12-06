FROM maven:3.9.6-amazoncorretto-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY config ./config

RUN mvn clean package -DskipTests \
    && rm -rf ~/.m2


FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/config ./config

EXPOSE 8080

# DO NOT start the app here — compose will start it
