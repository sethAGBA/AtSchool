# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy gradle setup files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Copy module build files
COPY server/build.gradle.kts server/
COPY shared/build.gradle.kts shared/
COPY composeApp/build.gradle.kts composeApp/

# Download dependencies (this layer will be cached unless build files change)
# We use 'classes' or 'dependencies' to ensure everything needed is downloaded.
# Running dependencies might be too verbose and slow for all configs, but it's safe.
# Alternatively, we can just run a dry build or help.
# 'dependencies' is the standard way to cache, but it downloads *everything*.
# Let's try to just resolve dependencies for :server:installDist
RUN ./gradlew :server:dependencies --no-daemon || true

# Copy source code
COPY server/src server/src
COPY shared/src shared/src

# Build the server distribution
RUN ./gradlew :server:installDist -x test --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the build artifacts from the build stage
COPY --from=build /app/server/build/install/server/ .

# Expose the port
EXPOSE 8080

# Environment variables with default values
ENV PORT=8080
ENV JWT_SECRET=change-me-in-production
ENV JDBC_URL=jdbc:postgresql://db:5432/atschool
ENV DB_USER=atschool
ENV DB_PASSWORD=atschool_pass

CMD ["./bin/server"]
