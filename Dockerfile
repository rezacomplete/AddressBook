# Multi-stage Dockerfile for AddressBook
# Stage 1: build the Spring Boot jar using the project's Maven wrapper when available
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /workspace

# Copy wrapper and maven config so wrapper can be used inside the image
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

# Try to use the wrapper; fall back to the image's mvn if wrapper is absent
RUN chmod +x ./mvnw || true
RUN ./mvnw -B -DskipTests package || mvn -B -DskipTests package

# Copy the resulting jar to a predictable location
RUN JAR_FILE=$(ls target/*.jar | grep -v "original" | head -n1) && cp "$JAR_FILE" /workspace/app.jar

# Stage 2: small runtime image using Java 21
FROM eclipse-temurin:21-jre

ARG JAVA_OPTS="-Xms256m -Xmx512m"
ENV JAVA_OPTS=${JAVA_OPTS}

WORKDIR /app

# Copy jar from builder
COPY --from=builder /workspace/app.jar /app/app.jar

# Create a non-root user and set ownership
RUN groupadd -r app && useradd -r -g app app && chown app:app /app/app.jar
USER app

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
