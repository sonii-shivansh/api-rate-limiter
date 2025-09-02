FROM openjdk:21-slim

WORKDIR /app

COPY target/rate-limiter-service-0.0.1-SNAPSHOT.jar rate-limiter-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "rate-limiter-service.jar"]