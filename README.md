# Todo Project: Account Service

This service is responsible for identity and role management within the Todo Microservice 
Project. 

Project has been setup to comply with [GDPR](https://gdpr-info.eu/) by deleting
personally-identifying information, but retaining database primary keys for service linking, auditing, etc.

## Table of Contents

- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#Installation)
    - [Deployment](#deployment)
- [Running The Tests](#running-the-tests)
- [Deployment](#deployment)
- [Built With](#built-with)
    - [Additional Features](#additional-features)
- [Versioning](#versioning)
- [License](#license)
 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

```
Java 8
Maven
MySQL DB 5 or Later
Lombok IDE Plugin
```

### Installation

Clone the repository locally, build and run via IDE Tools or build to JAR artifact and run via command line.

Additionally, supply the application with database information via command line arguments:
```
    spring.datasource.url=jdbc:mysql://{YOUR_DB_INSTANCE}:3306/{YOUR_SCHEMA}?useSSL=false;spring.datasource.username={APP_NAME};spring.datasource.password={APP_PASSWORD}
```

Access server on http://localhost:8080

Access API Documentation at http://localhost:8080/swagger-ui.html

## Running the tests

All tests for this service are unit tests and run without the Spring Context. 
Test coverage has Lombok generated methods in mind and many of the classes 
where testing wouldn't be appropriate (configuration, security, logging classes) or DAO 
classes have been ignored in Jacoco coverage reports and in SonarQube.

To run the tests if using IntelliJ:

1. Load the `Unit Tests` run configuration and run with code coverage
to execute all unit tests and generate the code coverage report

To run via Maven:

1. Execute `mvn test` and Maven will run the tests with HTML Jacoco code coverage
reports generated in target/site/jacoco


## Deployment

_**Coming soon**_

## Built With

* [Spring Boot 2.3.0](https://spring.io/projects/spring-boot) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Lombok](https://projectlombok.org/) - Used to generate java boilerplate code
* [Springdoc Swagger](https://springdoc.org/) - REST API Documentation
* [Model Mapper](http://modelmapper.org/) - Automatic DTO (Data Transfer Object) mapping
* [Flyway](https://flywaydb.org/) - Database Schema Versioning and Updates

### Additional Features
* Spring Data JPA
* Spring Data Kafka
* Spring AOP (Aspect Oriented Programming)
* Log4j2/SLF4J
* Flyway

### External Tools

List of tools that were used in development but not directly tied to the project 

* [Sonarqube](https://www.sonarqube.org/) - Code quality server
* [Sonarlint](https://www.sonarlint.org/) - Code quality linting tool
* [Nexus Repository](https://www.sonatype.com/nexus-repository-oss) - Shared artifacts repository
* [Jenkins](https://www.jenkins.io/) - Automated build and testing

## Versioning

This project uses [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/jrstubbington/todo-account-service/tags). 

## Authors

* **James Stubbington** - *Initial work* - [jrstubbington](https://github.com/jrstubbington)

See also the list of [contributors](https://github.com/jrstubbington/Todo-Account-Service/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details