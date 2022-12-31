# Blaze Ash

This is a practice project about automatic code generation. 

## Description

You can use gradle tasks to generate a lot of CRUD code. For example Controller, Service, Entity, Dao and SQL file.

## Demo

Coming soon...

## VS. 

## Requirement
- JDK 11+ ([Amazon Corretto 11](https://docs.aws.amazon.com/ja_jp/corretto/latest/corretto-11-ug/downloads-list.html) or [OpenJDK 11](https://www.oracle.com/technetwork/java/javase/downloads/index.html))
- [AWS CLI 2](https://docs.aws.amazon.com/ja_jp/cli/latest/userguide/install-cliv2.html)
- Docker 19.03.0+ ([Mac](https://docs.docker.com/docker-for-mac/) or [Windows](https://docs.docker.com/docker-for-windows/))
- [Docker Compose](https://docs.docker.com/compose/install/)

## Usage

## Command

### 1. Doma Gen
* Generate Code
    ```
    ./gradlew genEntity -P TABLE_NAME={TABLE_NAME}
    ./gradlew genResource -P TABLE_NAME={TABLE_NAME}
    ./gradlew genController -P TABLE_NAME={TABLE_NAME}
    ./gradlew genService -P TABLE_NAME={TABLE_NAME}
    ./gradlew genRepository -P TABLE_NAME={TABLE_NAME}
    ./gradlew genDao -P TABLE_NAME={TABLE_NAME}
    ```
#### 2. Docker
* build docker image
    ```
    docker build -t blazeash-api:latest .
    ```

#### 3. JAVA
* Code Format
    ```
    ./gradlew spotlessApply
    ```

* build project
    ```
    ./gradlew build
    ```

## Contribution

## Licence

Copyright © 2021 Blazeash inc. All Rights Reserved.

## Author

[yuanmz9432](https://github.com/yuanmz9432)
