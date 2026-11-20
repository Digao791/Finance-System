# Development Guide

## Prerequisites

- Java 21
- Maven via `./mvnw` (Maven 3.10.1)
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

On startup, Flyway applies migrations from
`src/main/resources/db/migration`. The initial migration,
`V1__create_transactions.sql`, creates the `transactions` table. Hibernate
is configured to validate rather than generate the database schema.

The API listens on Spring Boot's default port, `8080`, and is available at
`http://localhost:8080/`.

Transaction endpoints are available under `http://localhost:8080/api/transactions`.
Create a transaction with:

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"type":"INCOME","amount":1250.50,"transactionDate":"2026-11-19","description":"Salary"}'
```

List transactions with:

```bash
curl http://localhost:8080/api/transactions
```

Filter the list by an inclusive transaction-date range with ISO `YYYY-MM-DD`
values:

```bash
curl "http://localhost:8080/api/transactions?from=2026-11-01&to=2026-11-20"
```

Either `from` or `to` can be used alone. Results are ordered by transaction date
descending, with transaction identifier descending as a tie-breaker. When both
parameters are present, `from` must be on or before `to`; otherwise the API
returns HTTP `400 Bad Request`.

Request a monthly summary with a valid `YYYY-MM` month:

```bash
curl "http://localhost:8080/api/transactions/summary?month=2026-11"
```

The month parameter must contain a valid calendar month. Malformed or invalid
values return HTTP `400 Bad Request`.

Update a transaction with:

```bash
curl -X PUT http://localhost:8080/api/transactions/1 \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","amount":42.50,"transactionDate":"2026-11-20","description":"Office supplies"}'
```

Delete a transaction with:

```bash
curl -X DELETE http://localhost:8080/api/transactions/1
```

Unknown transaction identifiers return HTTP `404 Not Found`. Successful
deletes return HTTP `204 No Content`.

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

The checked-in JUnit tests cover transaction request validation and
`TransactionService` creation and listing behavior. They verify required fields,
amount precision and limits, description length, entity-to-response mapping,
and empty repository results. The automated test-generation workflow may add
separate `*Test.java` classes rather than modifying existing tests.

Build a package:

```bash
./mvnw -B package
```

The API Docker image uses a two-stage build: Maven packages the JAR, then
Eclipse Temurin Java 21 JRE runs `app.jar`.

## CI/CD

The Continuous Integration workflow runs on pushes and pull requests to
`main`. It sets up Java 21, runs `mvn --batch-mode test`, and packages the
application with `mvn -B package --file pom.xml`.

The Tests Generation workflow runs for source changes in `main` or manually.
In `changes` mode it analyzes modified production Java sources; in `missing`
mode it examines repository-wide coverage gaps. The test-generator agent
inspects existing tests before creating distinct new JUnit test classes ending
in `Test`. Application code, migrations, build configuration, and existing
tests are read-only.

The workflow checks out the exact workflow revision and uses Node.js 24 with
GitHub Copilot CLI. Its test-generator agent can use view, grep, glob,
skill, create, `edit`, and `apply_patch` tools with read and write access.
The workflow prepares a source-file list, generates tests, removes the
temporary list, and verifies that the Git worktree and index contain no
existing-file modifications. Only untracked regular `*Test.java` files under
`src/test/java/` are authorized. If validated tests exist, Java 21 is set up
and `bash ./mvnw -B test` is run.

Generated tests and Surefire reports are uploaded with
`actions/upload-artifact@v4`. A separate pull-request job downloads the
validated tests, copies them into a checkout of `main`, and creates an
`automation/tests-${MODE}` branch with a pull request. The branch is deleted
after it is merged or closed. The workflow stops if a test path already exists
on `main`.

The Pull Request Summary workflow runs for non-draft pull requests targeting
`main`. It checks out the trusted base revision, obtains the pull request diff,
sets up Node.js 24, and installs GitHub Copilot CLI. The
`pr-summary` agent summarizes the change and any test impact in English. The
workflow verifies that the pull request head has not advanced, then creates or
updates a bot comment marked with `<!-- finance-system-pr-summary -->`.

The Update project documentation workflow runs on pushes to `main` and by
manual dispatch. It compares the push with its base commit, summarizes the
changes with GitHub Copilot CLI, and produces a documentation patch for the
four maintained documentation files only when the resulting repository state
warrants a meaningful update. The project-documentation skill requires the
agent to update only affected sections and leave documentation unchanged for
formatting, whitespace, internal refactoring, routine test changes, and
changes whose behavior is not supported by the diff. A follow-up publishing
job downloads that patch, applies it to the generated revision, and commits
and pushes the documentation update to `main` only when the patch contains
changes.