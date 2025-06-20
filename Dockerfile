# Use Java 21 as the base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the Maven pom.xml and install dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Make the maven wrapper executable
RUN chmod +x ./mvnw
# Download dependencies (this layer will be cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Use a smaller JRE runtime as the base image for the final image
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=0 /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]