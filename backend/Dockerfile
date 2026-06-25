# ============================================================
# Stage 1: Maven build
# ============================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy Maven settings with Aliyun mirror for faster downloads
COPY settings-docker.xml /root/.m2/settings.xml
COPY pom.xml .
# Download dependencies first for layer caching
RUN mvn dependency:go-offline -B -q

COPY src/ src/
RUN mvn package -DskipTests -B -q

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# Install wget for health check
RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /build/target/*.jar app.jar

# Expose port
EXPOSE 8080

# JVM options (configurable via environment)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Health check
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

USER appuser

ENTRYPOINT java ${JAVA_OPTS} -jar app.jar
