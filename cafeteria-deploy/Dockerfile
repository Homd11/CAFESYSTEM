# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy MySQL connector JAR
COPY lib/mysql-connector-j-9.4.0.jar /app/lib/

# Copy source code
COPY src/ /app/src/

# Create build directory
RUN mkdir -p /app/build

# Compile Java application - find and compile all .java files
RUN find /app/src -name "*.java" -exec javac -cp "/app/lib/*" -d /app/build {} +

# Set classpath and run the application
CMD ["java", "-cp", "/app/build:/app/lib/*", "Main"]
