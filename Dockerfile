FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests -Dmaven.test.skip=true
EXPOSE 8080
CMD ["java", "-jar", "target/ecommerce-0.0.1-SNAPSHOT.jar"]