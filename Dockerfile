FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 10001 buildpilot
COPY --from=build /workspace/target/buildpilot-*.jar app.jar
USER buildpilot
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
