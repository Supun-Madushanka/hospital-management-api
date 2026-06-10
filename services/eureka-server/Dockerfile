FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml ./pom.xml

# Copy ALL service poms (needed by parent)
COPY services/eureka-server/pom.xml ./services/eureka-server/pom.xml
COPY services/config-server/pom.xml ./services/config-server/pom.xml
COPY services/api-gateway/pom.xml ./services/api-gateway/pom.xml
COPY services/auth-service/pom.xml ./services/auth-service/pom.xml
COPY services/user-service/pom.xml ./services/user-service/pom.xml
COPY services/appointment-service/pom.xml ./services/appointment-service/pom.xml

# Copy only this service source
COPY services/eureka-server/src ./services/eureka-server/src

RUN mvn clean package -pl services/eureka-server -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/services/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]