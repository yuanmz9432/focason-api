# =====================================================
# Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
# =====================================================
FROM openjdk:11-alpine
MAINTAINER Yuan
ARG JAR_FILE=release/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]