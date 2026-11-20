# Application Overview

## Purpose

Finance System is an early-stage Spring Boot application intended to become a
platform for managing financial information. The implemented code provides an
application bootstrap, a simple availability check, and REST operations for
creating and listing financial transactions.

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

The API exposes transaction operations at `/api/transactions`:

- `POST /api/transactions` accepts a validated transaction and returns the
  persisted response with HTTP `201 Created`.
- `GET /api/transactions` returns all persisted transactions.

A create request contains the transaction type, amount, date, and optional
description:

```json
{
  "type": "INCOME",
  "amount": 1250.50,
  "transactionDate": "2026-11-19",
  "description": "Salary"
}
```

The amount must be positive and has up to 17 integer digits and two fraction
digits. Type and transaction date are required, and descriptions are limited
to 255 characters. Responses include the generated identifier and the currency,
which is fixed to `BRL`.

The application validates the JPA schema on startup because
`spring.jpa.hibernate.ddl-auto` is set to `validate`. Flyway is included for
database migrations. Flyway migration `V1__create_transactions.sql` creates
the `transactions` table with:

- An identity primary key and the required transaction date and positive
  amount.
- An optional text description.
- A `type` restricted to `INCOME` or `EXPENSE`.
- A currency limited to `BRL`, with `BRL` as the database default.

## Key components

- `FinanceSystemApplication` bootstraps Spring Boot.
- `health` is a REST controller mapped to `/` and handles health checks.
- `TransactionController` exposes create and list operations at
  `/api/transactions`.
- `TransactionService` creates and persists transactions and converts entities
  to response DTOs.
- `TransactionRepository` provides Spring Data JPA persistence operations.
- `Transaction` and `TransactionType` map financial records and their
  `INCOME`/`EXPENSE` values to the database.
- `CreateTransactionRequest` and `TransactionResponse` define the API input
  and output shapes.
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
- `.github/workflows/tests-generation.yml` generates tests for changed Java
  sources or searches the repository for coverage gaps. It checks out the
  exact workflow revision, uses Node.js 24 for GitHub Copilot CLI, and
  prepares a changed-file or repository-wide production source list according
  to the `changes` or `missing` mode. The test-generator session is given
  view, search, glob, skill, create, `edit`, and `apply_patch` tools, with
  read and write access.
- The test-generator agent follows `.github/skills/test-generation/SKILL.md`,
  preserving existing files and placing additional scenarios in distinct new
  `*Test.java` classes under `src/test/java/`. The workflow rejects edits to
  existing or indexed files and unauthorized outputs, runs Maven tests when
  validated test files exist, and uploads generated tests with Surefire reports.
- Validated generated tests are downloaded by a dedicated pull-request job,
  copied into a checkout of `main`, and submitted by
  `peter-evans/create-pull-request@v8` without overwriting existing tests.
- `.github/agents/test-generator.agent.md` defines changed-file and
  repository-wide test-generation modes and requires the agent to preserve
  existing project files.
- `.github/skills/test-generation/SKILL.md` defines the test-generation scope,
  conventions, and verification responsibilities used by the agent.
- The checked-in test suite currently contains no Spring Boot context-load test;
  automated test generation can add focused JUnit test classes without changing
  existing source or test files.

## Current limitations

No reporting, updating or deleting transactions, authentication, or user
account-specific data isolation are implemented. The current transaction API
supports creation and listing only.