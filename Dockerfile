FROM openjdk:11
MAINTAINER Yuan
ARG JAR_FILE=release/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]