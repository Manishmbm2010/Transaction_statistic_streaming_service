FROM openjdk:8-jdk-alpine
COPY target/statistics-0.0.1.jar statistics.jar
ENTRYPOINT java -jar statistics.jar
