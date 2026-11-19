# Finance System

Finance System is a Spring Boot foundation for a financial management
application. The current API exposes a root health endpoint, and the project
is configured for PostgreSQL persistence, Flyway migrations, Docker Compose,
Maven builds, and automated tests.

## Features

- Spring Boot 4.1 application running on Java 21
- Health endpoint at `GET /`, which returns `Ok`
- PostgreSQL 18 database configuration
- JPA validation mode and Flyway PostgreSQL support
- Docker Compose startup for the API and database
- Maven test and build workflows
- Automated test-generation and documentation-update/publishing workflows

## Development tools

- Java 21 (Eclipse Temurin in Docker and CI)
- Maven, through the Maven Wrapper
- PostgreSQL 18 Alpine
- Docker and Docker Compose
- GitHub Actions
- Spring Boot, Spring Data JPA, Flyway, Lombok, and JUnit 5

## Repository structure

- `src/main/java/` - application source code
- `src/main/resources/` - Spring configuration
- `src/test/java/` - Spring context and unit tests
- `infra/` - API container definitions
- `.github/workflows/` - CI, test generation, and documentation automation
- `compose.yml` - local API and PostgreSQL services
- `pom.xml` - Maven dependencies and build configuration
- `docs/` - project documentation

## Documentation

- [Project history](docs/PROJECT-HISTORY.md) — Summarizes changes across
  the project by date.
- [Application overview](docs/APPLICATION-OVERVIEW.md) — Explains the
  application's purpose, main features, and how it works.
- [Development guide](docs/DEVELOPMENT-GUIDE.md) — Covers prerequisites,
  development tools, configuration, and how to run and test the application.