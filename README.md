# lemonico-api

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

#### 1. generate code with gen
```
./gradlew genAll -P TABLE_NAME={TABLE_NAME} -P FOLDER={FOLDER_NAME}
```

#### 2. build docker image
```
docker build -t {CONTAINER_NAME}:{TAG} .
```
### Before commit & push the code to git, we should run No.3 and No.4 commands.To make sure build process success.

#### 3. Java code format
```
./gradlew spotlessApply
```

#### 4. build project
```
./gradlew build
```

## Contribution

## Licence

Copyright © 2021 Lemonico inc. All Rights Reserved.

## Author

[yuanmz9432](https://github.com/yuanmz9432)
