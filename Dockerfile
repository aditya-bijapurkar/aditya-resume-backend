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

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD ["/busybox/sh", "-c", "wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1"]

ENTRYPOINT ["java", "-jar", "app.jar"]