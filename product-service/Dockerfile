# 1. The Base Image
# We use Java 21 (Eclipse Temurin) on a lightweight Linux (Alpine)
FROM eclipse-temurin:21-jdk-alpine

# 2. The Setup
# Create a folder called "/app" inside the container
WORKDIR /app

# 3. The Copy
# Take the JAR file from your laptop's "target" folder and put it in the container
COPY target/*.jar app.jar

# 4. The Start Command
# When the container turns on, run this Java command
ENTRYPOINT ["java", "-jar", "app.jar"]