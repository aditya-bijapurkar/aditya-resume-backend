FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY config ./config

RUN mvn clean package -DskipTests && \
    rm -rf ~/.m2

FROM --platform=linux/amd64 gcr.io/distroless/java17-debian11:nonroot

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

COPY --from=build /app/config ./config

USER 1001:1001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]