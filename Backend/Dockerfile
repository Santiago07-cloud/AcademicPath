# ── Stage 1: Build ────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# ── Stage 2: Runtime ───────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Seguridad: usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/target/Backend-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser

# Render inyecta PORT automáticamente
EXPOSE 8080

# JVM optimizada para contenedores con poca RAM (Render free tier = 512MB)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

