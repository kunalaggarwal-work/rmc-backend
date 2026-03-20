FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the JAR from target directory to the container
COPY target/rmc-0.0.1-SNAPSHOT.jar rmc-app.jar

# Expose the port that the application runs on
EXPOSE 9091

# Run the application
ENTRYPOINT ["java", "-jar", "rmc-app.jar"]

