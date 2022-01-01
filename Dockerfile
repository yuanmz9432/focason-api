# =====================================================
# Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
# =====================================================
FROM amazoncorretto:11-alpine
MAINTAINER Yuan
ARG JAR_FILE=release/*.jar
COPY ${JAR_FILE} app.jar
#ADD build/libs/lemonico-api-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]