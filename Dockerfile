FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY config ./config

RUN mvn clean package -DskipTests

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

ARG SMTP_PASSWORD
ARG DB_USER
ARG DB_PORT
ARG DB_NAME
ARG DB_PASSWORD
ARG GOOGLE_ACCESS_KEY_BASE64

RUN apk add --no-cache curl

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

COPY --from=build /app/config ./config

RUN mkdir -p tokens

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV DB_HOST=${DB_HOST}
ENV DB_USER=${DB_USER}
ENV DB_PORT=${DB_PORT}
ENV DB_NAME=${DB_NAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV SMTP_PASSWORD=${SMTP_PASSWORD}
ENV GOOGLE_ACCESS_KEY_BASE64=${GOOGLE_ACCESS_KEY_BASE64}

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]