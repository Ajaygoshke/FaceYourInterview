FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8080


# Run the application
CMD ["java", "-jar", "FYI-1-0.0.1-SNAPSHOT.jar"]