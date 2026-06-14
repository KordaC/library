FROM gradle:8.10.2-jdk17-alpine AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src src
RUN gradle bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/library-backend.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=cloud"]
