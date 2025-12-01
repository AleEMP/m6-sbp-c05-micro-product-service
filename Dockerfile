FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar /app/product-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]