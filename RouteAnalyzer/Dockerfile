# Use Eclipse Temurin JDK 22 as the base image
FROM eclipse-temurin:22-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Kotlin application JAR file into the container
COPY build/libs/RouteAnalyzer-1.0-SNAPSHOT.jar RouteAnalyzer.jar

# Expose a volume for mounting input files at runtime
VOLUME /app

# Set the default command to run the application
CMD ["java", "-jar", "RouteAnalyzer.jar", "/app/custom-parameters.yml", "/app/waypoints.csv"]
