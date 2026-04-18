# ── Build stage ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy wrapper + POM first so dependency layer is cached separately from source
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:resolve -q --no-transfer-progress

# Copy source and package
COPY src src
RUN ./mvnw package -DskipTests -q --no-transfer-progress

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
