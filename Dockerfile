# =====================================================
# Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
# =====================================================
FROM amazoncorretto:11-alpine
MAINTAINER Yuan

# バーコードを生成するため、FontConfigをinstall必要！！！
RUN apk add --update;apk add ttf-dejavu;apk add fontconfig

# application.yml env
ENV DB_HOST="127.0.0.1"
ENV DB_PORT="3306"
ENV DB_SCHEMA="blazeash"
ENV DB_USERNAME="root"
ENV DB_PASSWORD="password"
ENV APP_PORT=80

EXPOSE 80 443
ADD build/libs/blazeash-api-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
