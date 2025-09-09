FROM openjdk:21-slim

WORKDIR /app

COPY target/product-service-0.0.1-SNAPSHOT.jar product-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "product-service.jar"]