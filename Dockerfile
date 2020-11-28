# WARNING: This container runs as root as it requires to consume the host Docker socket!

FROM openjdk:14-alpine
WORKDIR /app
COPY ubuntu-stats-app/target/*.jar ./app.jar
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]

EXPOSE 8080