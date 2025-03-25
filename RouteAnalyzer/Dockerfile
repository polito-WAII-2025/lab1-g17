# Use Eclipse Temurin JDK 22 as the base image
FROM eclipse-temurin:22-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and the source code into the container
COPY gradlew gradlew
COPY gradle gradle
COPY src src
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

# Run the Gradle wrapper to build the application
RUN ./gradlew clean build

# Expose a volume for mounting input files at runtime
VOLUME /app

# Set the default command to run the application
CMD ["java", "-jar", "build/libs/RouteAnalyzer-1.0-SNAPSHOT.jar", "/app/custom-parameters.yml", "/app/waypoints.csv"]
