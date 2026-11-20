# Finance System

Finance System is a financial management application with a Spring Boot API and
React web interface. The API exposes an availability check and transaction
creation, listing, update, delete, and monthly-summary operations. The project
uses PostgreSQL persistence, Flyway migrations, Docker Compose, Maven builds,
and a Vite frontend.

## Features

- Spring Boot 4.1 application running on Java 21
- Health endpoint at `GET /`, which returns `Ok`
- Transaction API for creating, listing with optional date-range filters,
  updating, deleting, and requesting monthly summaries for BRL financial
  transactions
- JPA transaction entity and repository with `INCOME` and `EXPENSE` types
- Validated transaction request and response DTOs
- PostgreSQL 18 database configuration
- JPA validation mode and Flyway PostgreSQL support
- Flyway transaction schema migration for income and expense records
- React 19 frontend with transaction management, period filtering, summaries,
  daily charts, and a containerized Nginx delivery
- Docker Compose startup for the API, PostgreSQL database, and web frontend
- Maven test and build workflows
- JUnit 5 coverage for transaction request validation and transaction service
  creation and listing behavior
- Automated test-generation that preserves existing files and uses JUnit
  test artifacts, validates generated tests, runs Maven, and can open a
  pull request with the new test classes. The test-generator session provides
  editing tools for generating new test files while existing project files
  remain read-only.
- Automated pull request summaries that post an English change summary as a
  comment on a pull request, updating the existing bot comment when the pull
  request changes.
- Automated documentation update and publishing workflows

## Development tools

- Java 21 (Eclipse Temurin in Docker and CI)
- Maven, through the Maven Wrapper
- PostgreSQL 18 Alpine
- Docker and Docker Compose
- Node.js 24, npm, React 19, Vite 8, TypeScript 7, Oxlint, and Nginx
- GitHub Actions
- GitHub Copilot CLI for test and documentation automation
- Spring Boot, Spring Data JPA, Flyway, Lombok, and JUnit 5
- Maven Surefire 3.5.5

## Repository structure

- `src/main/java/` - application source code
- `src/main/resources/` - Spring configuration and database migrations
- `src/test/java/` - JUnit test classes and generated test coverage
- `frontend/` - React, TypeScript, and Vite web application
- `infra/` - API container definitions
- `.github/workflows/` - CI, test generation, and documentation automation
- `.github/agents/` - repository automation agent definitions
- `.github/skills/` - instructions used by automation agents
- `compose.yml` - local API, PostgreSQL, and web services
- `pom.xml` - Maven dependencies and build configuration
- `docs/` - project documentation

## Documentation

- [Project history](docs/PROJECT-HISTORY.md) — Summarizes changes across
  the project by date.
- [Application overview](docs/APPLICATION-OVERVIEW.md) — Explains the
  application's purpose, main features, and how it works.
- [Development guide](docs/DEVELOPMENT-GUIDE.md) — Covers prerequisites,
  development tools, configuration, and how to run and test the application.