# Application Overview

## Purpose

Finance System is an early-stage financial management application. It combines
a Spring Boot REST API with a React web interface for recording and reviewing
BRL income and expense transactions.

## Users

The application is designed for users who need to manage financial records
through a browser or API. No authentication, authorization, user roles, or
account-specific workflows are implemented yet.

## Main workflow

The web interface starts on port `3000` in the Docker environment and serves
the React application through Nginx. The API starts on port `8080`, and Nginx
proxies `/api/` requests to the API container. The local PostgreSQL database
supplies the configured datasource.

In the browser, users can switch between transaction and chart views, filter
records by month, date range, or all dates through today, review income,
expense, and balance totals, and create, edit, or delete transactions. Settings
and profile navigation remain placeholders.

Clients can also verify API availability with:

```text
GET /
Response: Ok
```

The API exposes transaction operations at `/api/transactions`:

- `POST /api/transactions` accepts a validated transaction and returns the
  persisted response with HTTP `201 Created`.
- `GET /api/transactions` lists persisted transactions, ordered by transaction
  date descending and then by identifier descending. The optional `from` and
  `to` query parameters accept ISO dates and restrict results to the inclusive
  date range. Supplying only `from` or only `to` is supported; when both are
  supplied, `from` must not be later than `to`.
- `GET /api/transactions/summary?month=YYYY-MM` returns a monthly summary.
  The month must use a valid `yyyy-MM` calendar value; malformed or invalid
  values return HTTP `400 Bad Request`. The response includes the month,
  income, expense, balance, and fixed `BRL` currency.
- `PUT /api/transactions/{id}` validates and replaces the transaction fields
  and returns the updated response.
- `DELETE /api/transactions/{id}` removes the transaction and returns
  HTTP `204 No Content`.

Update and delete requests for an unknown transaction identifier return HTTP
`404 Not Found`.

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
- `TransactionController` exposes create, list, monthly-summary, update, and
  delete operations at `/api/transactions`.
- `TransactionService` creates, updates, and deletes transactions, converts
  entities to response DTOs, rejects unknown identifiers with HTTP `404`,
  filters and sorts transaction listings, and builds monthly summaries.
- `TransactionRepository` provides Spring Data JPA persistence operations,
  including inclusive date-from, date-to, and date-range queries, plus a
  start-inclusive and end-exclusive month query.
- `Transaction` and `TransactionType` map financial records and their
  `INCOME`/`EXPENSE` values to the database.
- `CreateTransactionRequest`, `TransactionResponse`, and
  `MonthlySummaryResponse` define the API input and output shapes.
- `application.yml` defines the Spring application name and local PostgreSQL
  datasource.
- `V1__create_transactions.sql` establishes the initial transactions table,
  including type, amount, currency, date, and description constraints.
- `compose.yml` runs PostgreSQL and the API together, with database health
  checking before the API starts, and adds the web service on port `3000`.
- `frontend/` contains the React 19, TypeScript, and Vite web application. It
  calls `/api/transactions`, supports month and date-range filtering, shows
  period totals and a daily chart, and provides transaction create, update,
  and delete controls.
- `vite.config.ts` proxies `/api` requests to `http://localhost:8080` during
  frontend development.
- `infra/Dockerfile.web` builds the frontend with Node.js 24 and Vite, then
  serves the static output through Nginx.
- `infra/nginx.conf` serves the SPA, provides SPA fallback routing, and proxies
  `/api/` requests to the API container.
- `pom.xml` declares Java 21, Spring Web MVC, Spring Data JPA, validation,
  PostgreSQL, Flyway, Lombok, test dependencies, and Maven Surefire 3.5.5.
- `infra/Dockerfile.api` builds the application with Maven and runs the
  resulting JAR in an Eclipse Temurin Java 21 runtime.
- `.github/workflows/update-documentation.yml` reviews push diffs for meaningful
  documentation changes, generates patches for only affected sections, and can
  publish those patches through a dedicated commit-and-push job.
- `.github/workflows/pr-summary.yml` summarizes pull request changes with
  GitHub Copilot CLI and publishes the English summary as a pull request
  comment. It runs against the trusted base revision, regenerates the summary
  when pull request commits change, and updates the existing bot comment.
- `.github/agents/pr-summary.agent.md` instructs the summary agent to derive
  the purpose and impact from the pull request diff and to report test changes
  or test evidence only when available.
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
- `CreateTransactionRequestValidationTest` verifies the maximum supported
  amount precision, required fields, positive amount constraints, fraction
  precision, and 255-character description limit.
- `TransactionServiceTest` verifies transaction persistence and response mapping
  for creation, maps multiple repository records to API responses, and verifies
  empty-list behavior.

## Current limitations

No reporting, authentication, or user account-specific data isolation are
implemented.