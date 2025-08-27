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
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:+UseStringDeduplication", \
    "-XX:+OptimizeStringConcat", \
    "-XX:+UseCompressedOops", \
    "-XX:+UseCompressedClassPointers", \
    "-Xms512m", \
    "-Xmx1024m", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]