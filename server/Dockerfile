# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS build

# Set the working directory
WORKDIR /app

# Copy the entire project to the container (needed for shared module)
COPY . .

# Build the server distribution (skipping tests for faster build)
RUN ./gradlew :server:installDist -x test --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the build artifacts from the build stage
# The path depends on the project name in settings.gradle.kts, usually defaults to directory name 'server'
COPY --from=build /app/server/build/install/server/ .

# Expose the port
EXPOSE 8080

# Environment variables with default values (can be overridden)
ENV PORT=8080
ENV JWT_SECRET=change-me-in-production
ENV JDBC_URL=jdbc:postgresql://db:5432/atschool
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres

# Run the application
# The script name in bin/ usually matches the project name 'server'
CMD ["./bin/server"]
