FROM maven:latest AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn package

FROM openjdk:17-jdk-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]