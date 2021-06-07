# Build and package
FROM maven:3.6.3-jdk-11-slim AS build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests

# Run
FROM openjdk:11-jre-slim
RUN mkdir /app
COPY --from=build /project/target/spring-boot-0.0.1-SNAPSHOT.jar /app/java-application.jar
WORKDIR /app
CMD "java" "-jar" "java-application.jar"
