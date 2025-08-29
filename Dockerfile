FROM maven:3.9.6-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY config ./config

RUN mvn clean package -DskipTests \
    -Dmaven.compiler.source=17 \
    -Dmaven.compiler.target=17 \
    && rm -rf ~/.m2

FROM gcr.io/distroless/java17-debian11:nonroot

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/config ./config

USER 1001:1001

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseSerialGC", \
    "-Xms128m", \
    "-Xmx512m", \
    "-XX:MaxMetaspaceSize=128m", \
    "-Dserver.tomcat.max-threads=20", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Djava.net.preferIPv4Stack=true", \
    "-jar", "app.jar"]