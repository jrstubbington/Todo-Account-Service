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

Access server on http://localhost:8080

## Running the tests

_**Coming soon**_

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

## Versioning

This project uses [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/jrstubbington/todo-account-service/tags). 

## Authors

* **James Stubbington** - *Initial work* - [jrstubbington](https://github.com/jrstubbington)

See also the list of [contributors](https://github.com/jrstubbington/Todo-Account-Service/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details