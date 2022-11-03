FROM adoptopenjdk/openjdk11:alpine-jre

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=release_login8081", "/app.jar"]