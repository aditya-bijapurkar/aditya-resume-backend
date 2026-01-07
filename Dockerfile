FROM maven:3.9.6-amazoncorretto-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY config ./config

RUN mvn clean package -DskipTests && rm -rf ~/.m2

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/config ./config

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=35.0", "-XX:InitialRAMPercentage=20.0", "-XX:MaxMetaspaceSize=160m", "-XX:CompressedClassSpaceSize=32m", "-XX:MaxDirectMemorySize=32m", "-XX:+UseG1GC", "-XX:+ExitOnOutOfMemoryError", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]