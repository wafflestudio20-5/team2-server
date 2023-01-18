FROM openjdk:17-jdk-slim
WORKDIR /home

COPY ./build/libs/*.jar app.jar

EXPOSE 5000

CMD ["java", "-Dspring.profiles.active=prod", "-Dcom.amazonaws.sdk.disableEc2Metadata=true", "-jar", "app.jar"]