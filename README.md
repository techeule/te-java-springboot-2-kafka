# Java Spring Boot with Kafka example project for [TechEule.com](https://techeule.com/)

> All provided paths in this file are relative to the root-folder
> of this git-repository.

At [TechEule.com](https://techeule.com/) you can find more info about this repository.

## Code

The Avro-Schemas are located at:
[`src/main/resources/avro-schemas/`](./src/main/resources/avro-schemas)

**Note**
> First, you have to compile this project using maven because
> the `AVRO`-classes are generated from the AVRO-Schema at
> [`src/main/resources/avro-schemas`](./src/main/resources/avro-schemas)
> using the _org.apache.avro :: avro-maven-plugin_

## Requirements

- JDK version 17 or newer
- Maven 3.8 or newer
- Docker
- Docker Compose

## How to set up the environment/infrastructure

Open the terminal

```shell
# navigate to environment/te-spring-kafka-docker
cd environment/te-spring-kafka-docker

# start the docker compose services
# the first time it will take several minutes to pull all needed docker images
docker-compose up -d
 ```

After the environment is booted you have to run the database migration.
This project support both database systems MySQL and Postgresql, but not at the same time.

### Flyway: MySQL/MariaDB

```shell
mvn -Dflyway.configFiles=src/main/flyway/flyway-mysql.properties flyway:migrate
```

### Flyway: Postgresql

```shell
mvn -Dflyway.configFiles=src/main/flyway/flyway-postgresql.properties flyway:migrate
```

## How to build and run the applicaton

### Maven build

Here you have to decide which database system do you want to use.

#### MySQL

```shell
mvn clean package --activate-profiles mysql,-postgresql
```

#### Postgresql

```shell
mvn clean package --activate-profiles -mysql,postgresql
```


### Application Start

Here you have to decide which database system do you want to use.

#### MySQL

##### Using Maven
```shell
mvn clean spring-boot:run --activate-profiles mysql,-postgresql -Dspring-boot.run.profiles=mysql 
```

##### Plain java
```shell
java -Dspring.profiles.active="mysql" -jar target/techeule-java-maven-template.jar 
```

#### Postgresql

##### Using Maven
```shell
mvn clean spring-boot:run --activate-profiles -mysql,postgresql -Dspring-boot.run.profiles=postgresql
```

##### Plain java
```shell
java -Dspring.profiles.active="postgresql" -jar target/techeule-java-maven-template.jar
```

## Resources

- [Spring Boot version 2](https://docs.spring.io/spring-boot/docs/2.7.8/reference/html/)
- [Apache AVRO](https://avro.apache.org/)
- [AssertJ](https://assertj.github.io/doc/)
- [JUnit5](https://junit.org/junit5/docs/5.9.2/user-guide/)
- [TechEule.com](https://techeule.com/)
