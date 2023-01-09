# =====================================================
# Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
# =====================================================
FROM amazoncorretto:11-alpine
MAINTAINER Yuan

# バーコードを生成するため、FontConfigをinstall必要！！！
RUN apk add --update;apk add ttf-dejavu;apk add fontconfig

# application.yml env
ENV APP_PORT=80
ENV DB_HOST=mysql.focason.com
ENV DB_PORT=3306
ENV DB_SCHEMA=focason
ENV DB_USERNAME=admin
ENV DB_PASSWORD=password

EXPOSE 80 443
ADD build/libs/focason-api-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
