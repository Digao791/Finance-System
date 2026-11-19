# Development Guide

## Prerequisites

- Java 21
- Maven via `./mvnw`
- PostgreSQL 18, either locally or through Docker Compose
- Docker and Docker Compose for the containerized API and database

## Initial setup

Use Java 21 and make the Maven Wrapper executable if needed:

```bash
chmod +x mvnw
```

The Spring configuration expects PostgreSQL at
`localhost:5432/finance` with the username and password `finance`. These are
development placeholders and should be replaced by environment-specific
secrets outside the repository.

## Run locally

Start PostgreSQL with Docker Compose:

```bash
docker compose up -d database
```

Run the Spring application:

```bash
./mvnw spring-boot:run
```

The API listens on Spring Boot's default port, `8080`, and is available at
`http://localhost:8080/`.

The complete container setup is also available:

```bash
docker compose up --build
```

Docker Compose maps the API to port `8080`, PostgreSQL to port `5432`, and
uses a persistent volume for database data.

## Build and test

Run the test suite:

```bash
./mvnw test
```

Build a package:

```bash
./mvnw -B package
```

The API Docker image uses a two-stage build: Maven packages the JAR, then
Eclipse Temurin Java 21 JRE runs `app.jar`.

## CI/CD

The Continuous Integration workflow runs on pushes and pull requests to
`main`. It sets up Java 21 and runs `mvn --batch-mode test`, then invokes
Maven with `pacakge`; the build step currently contains a misspelling that
prevents this command from resolving a lifecycle phase.

The Tests Generation workflow runs for source changes in `main` or manually.
It generates tests from changed Java sources, validates generated files,
runs Maven tests, uploads the results, and opens pull requests containing
generated tests.

The Update project documentation workflow runs on pushes to `main` and by
manual dispatch. It compares the push with its base commit, summarizes the
changes with GitHub Copilot CLI, and produces a documentation patch for the
four maintained documentation files.