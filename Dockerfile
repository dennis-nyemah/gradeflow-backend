# ─────────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Pull dependencies in a separate layer so Docker cache reuses it
# unless pom.xml changes
RUN ./mvnw dependency:go-offline --no-transfer-progress

COPY src ./src
RUN ./mvnw clean package -DskipTests --no-transfer-progress

# ─────────────────────────────────────────────
# Stage 2: Run
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Render injects PORT at runtime; default to 8080 locally
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]