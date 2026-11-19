# Application Overview

## Purpose

Finance System is an early-stage Spring Boot application intended to become a
platform for managing financial information. The implemented code currently
provides an application bootstrap and a simple availability check; financial
domain features have not yet been implemented.

## Users

The application is designed to support users who need to access financial
management capabilities through an API. No authentication, authorization,
user roles, or account-specific workflows are implemented yet.

## Main workflow

The API starts as a Spring Boot application on port `8080` in the Docker
environment. The local PostgreSQL database supplies the configured datasource.
Clients can verify availability with:

```text
GET /
Response: Ok
```

The application validates the JPA schema on startup because
`spring.jpa.hibernate.ddl-auto` is set to `validate`. Flyway is included for
database migrations, although no migration files are currently present.

## Key components

- `FinanceSystemApplication` bootstraps Spring Boot.
- `health` is a REST controller mapped to `/` and handles health checks.
- `application.yml` defines the Spring application name and local PostgreSQL
  datasource.
- `compose.yml` runs PostgreSQL and the API together, with database health
  checking before the API starts.
- `pom.xml` declares Java 21, Spring Web MVC, Spring Data JPA, validation,
  PostgreSQL, Flyway, Lombok, and test dependencies.
- `infra/Dockerfile.api` builds the application with Maven and runs the
  resulting JAR in an Eclipse Temurin Java 21 runtime.

## Current limitations

No financial records, reporting, transaction processing, authentication,
database entities, or migration scripts are implemented in the current
source tree.