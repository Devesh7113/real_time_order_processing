# Render: choose "Docker", set Root Directory to `backend/real_time_order_processing` (or this folder relative to your repo).
# Uses Gradle image because `gradle-wrapper.jar` may not be committed; matches project's Java 21 toolchain.

FROM gradle:8.14.3-jdk21-alpine AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle bootJar --no-daemon \
    && cp "$(find build/libs -maxdepth 1 -name '*.jar' ! -name '*-plain.jar' | head -n1)" /app/application.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/application.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
