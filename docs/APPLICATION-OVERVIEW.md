# Application Overview

## Purpose

Finance System is an early-stage Spring Boot application intended to become a
platform for managing financial information. The implemented code provides an
application bootstrap, a simple availability check, and the initial database
schema for financial transactions.

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
database migrations. Flyway migration `V1__create_transactions.sql` creates
the `transactions` table with:

- An identity primary key and the required transaction date and positive
  amount.
- An optional text description.
- A `type` restricted to `INCOME` or `EXPENSE`.
- A currency limited to `BRL`, with `BRL` as the database default.

No Java entity or REST endpoint for managing transactions is implemented yet.

## Key components

- `FinanceSystemApplication` bootstraps Spring Boot.
- `health` is a REST controller mapped to `/` and handles health checks.
- `application.yml` defines the Spring application name and local PostgreSQL
  datasource.
- `V1__create_transactions.sql` establishes the initial transactions table,
  including type, amount, currency, date, and description constraints.
- `compose.yml` runs PostgreSQL and the API together, with database health
  checking before the API starts.
- `pom.xml` declares Java 21, Spring Web MVC, Spring Data JPA, validation,
  PostgreSQL, Flyway, Lombok, and test dependencies.
- `infra/Dockerfile.api` builds the application with Maven and runs the
  resulting JAR in an Eclipse Temurin Java 21 runtime.
- `.github/workflows/update-documentation.yml` generates documentation patches
  for pushes to `main` and can publish those patches through a dedicated
  commit-and-push job.

## Current limitations

No reporting, transaction processing, authentication, user accounts, or
transaction entity and API implementations are present in the current source
tree. The only supported financial data operation is the schema migration that
creates the `transactions` table.