# Multi-stage build for optimized image size
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (cache dependencies)
COPY pom.xml .

# Remove go-offline (too unstable)
# RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root user for security
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 6666

# Health check (optional)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:6666/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
