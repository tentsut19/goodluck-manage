# Getting Started


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/maven-plugin/)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/htmlsingle/#boot-features-mongodb)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)


### Run local
spring-boot:run -Dspring-boot.run.profiles=local

### Run prod
spring-boot:run -Dspring-boot.run.profiles=prod

### Build dev And Run
mvn clean package -Dspring.profiles.active=dev spring-boot:repackage && java -jar target/cabsat-1.0.0-SNAPSHOT.jar

### Build uat And Run
mvn clean package -Dspring.profiles.active=uat spring-boot:repackage && java -jar target/cabsat-1.0.0-SNAPSHOT.jar

### Build
mvn clean install

mvn clean package

### Restart Service
ssh to server

move jar -> /api/deploy/uat
move jar -> /api/deploy/prod

restart service

sudo systemctl restart cabsat-uat
sudo systemctl restart cabsat-prod

