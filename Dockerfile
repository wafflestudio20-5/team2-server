FROM openjdk:17-jdk-slim
WORKDIR /home

COPY ./build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "-Dspring.profiles.active=prod"]
