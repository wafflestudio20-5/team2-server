FROM openjdk:17-jdk-slim
WORKDIR /home

COPY ./build/libs/*.jar app.jar

EXPOSE 5000

CMD ["java", "-jar", "app.jar", "-Dspring.profiles.active=prod"]
