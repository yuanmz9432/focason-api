# Focason API

This is a java project for practice. 

## Description

Write descriptions here...

## Badge
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-v2.7.6-brightgreen)
![Gradle](https://img.shields.io/badge/Gradle-v6.9.1-lightgrey)
![Java](https://img.shields.io/badge/Amazon%20Corretto-11-orange)
![MySQL](https://img.shields.io/badge/MySQL-v5.7.x-blue)

![Jupiter](https://img.shields.io/badge/Jupiter-v5.9.1-%230057b7)
![jsonwebtoken](https://img.shields.io/badge/jsonwebtoken-v0.9.1-brightgreen)
![Doma2](https://img.shields.io/badge/Doma2-v2.53.0-blue)
![Doma2 Gen](https://img.shields.io/badge/Doma2%20Gen-v2.28.0-blue)
![AWS CLI 2](https://img.shields.io/badge/AWS%20CLI%20-2-orange)
![Lombok](https://img.shields.io/badge/Lombok-v1.18.24-red)
![Spotless](https://img.shields.io/badge/spotless-v5.16.0-orange)

![Postman](https://img.shields.io/badge/Postman-v10.6.7-orange)
![Docker Desktop](https://img.shields.io/badge/Docker%20Desktop-v19.03.8-blue)
![Navicat](https://img.shields.io/badge/Navicat-16-yellow)
![Git](https://img.shields.io/badge/Git-v2.39.0-orange)
![Github Desktop](https://img.shields.io/badge/Github%20Desktop-v3.1.3-purple)
![Swagger OpenAPI](https://img.shields.io/badge/Swagger%20OpenAPI-v3.0.3-%2338b832)

## Mock

Coming soon...

## Install

### 1. Flyway Setup

Create Database.
```shell
# login into mysql
$ mysql -u root -p
```
```sql
# create database focason
CREATE DATABASE focason;
```

Update **./gradle.properties** file.
```text
DB_HOST=localhost
DB_PORT=3306
DB_SCHEMA=focason
DB_USERNAME=root
DB_PASSWORD=password
```

Run flyway
```shell
# flyway clean
$ ./gradlew flywayClean
```
```shell
# flyway info
$ ./gradlew flywayInfo
```
```shell
# flyway migrate
$ ./gradlew flywayMigrate
```

### 2. Build Setup
```shell
# spotless apply
$ ./gradlew spotlessApply
```
```shell
# spotless check
$ ./gradlew spotlessCheck
```
```shell
# build
$ ./gradlew build
```
```shell
# package
$ ./gradlew bootJar
```

### 3. Doma2 Gen Setup

Update **./gradle.properties** file.
```text
GEN_TARGET_TABLE={table_name}
```

Generate code
```shell
# controller
$ ./gradlew genController
```
```shell
# service
$ ./gradlew genService
```
```shell
# resource
$ ./gradlew genResource
```
```shell
# entity
$ ./gradlew genEntity
```
```shell
# dao
$ ./gradlew genDao
```
```shell
# repository
$ ./gradlew genRepository
```

### 4. Docker Setup

Update **./Dockerfile** file.
```text
ENV APP_PORT=80
ENV DB_HOST=mysql.focason.com
ENV DB_PORT=3306
ENV DB_SCHEMA=focason
ENV DB_USERNAME=admin
ENV DB_PASSWORD=password
```

Build docker image
```shell
docker build -t focason-api:latest .
```

Update **./docker-compose** file.
```text
environment:
  APP_PORT: 80
  DB_HOST: mysql.focason.com
  DB_PORT: 3306
  DB_NAME: focason
  DB_USERNAME: admin
  DB_PASSWORD: password
```

Build docker container
```shell
docker-compose up
```

## Maintainers

[yuanmz9432](https://github.com/yuanmz9432)
## Contribution

## Licence

Copyright © 2023 Focason inc. All Rights Reserved.
