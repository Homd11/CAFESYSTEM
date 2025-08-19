# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy MySQL connector JAR
COPY lib/mysql-connector-j-9.4.0.jar /app/lib/

# Copy source code
COPY src/ /app/src/

# Compile Java application
RUN javac -cp "/app/lib/*" -d /app/build src/**/*.java

# Set classpath and run the application
CMD ["java", "-cp", "/app/build:/app/lib/*", "Main"]
