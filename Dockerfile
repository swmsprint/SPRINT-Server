FROM adoptopenjdk/openjdk11:alpine-jre

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

RUN apk add -U tzdata
ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=release_loginecs8081", "/app.jar"]