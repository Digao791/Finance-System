# Project History

## 2026-11-19

- Reworked the test-generation workflow to check out the exact workflow
  revision, select `changes` or `missing` mode, and set up Node.js 24 before
  running GitHub Copilot CLI. Changed-source and repository-wide file lists
  are now prepared in dedicated steps.
- Tightened test generation so the workflow rejects modifications to existing
  or indexed files, accepts only new `*Test.java` files under
  `src/test/java/`, runs Maven only when tests were generated, and packages
  validated tests with Maven Surefire reports for a pull request.
- Updated the test-generator agent and test-generation skill to inspect
  existing coverage, support changed-file and repository-wide modes, and
  create distinct JUnit test classes while preserving existing project files.
- Fixed the test-generation workflow's generated-test artifact upload by
  replacing the invalid `actions/upload-artifacts@v4` action reference with
  the supported `actions/upload-artifact@v4` action.
- Added the transaction application model and REST API. Transactions use
  `INCOME` or `EXPENSE` types, positive BRL amounts, a required date, and an
  optional 255-character description.
- Added transaction request and response DTOs with validation for required
  fields, positive amounts, numeric precision, and description length.
- Added a Spring Data JPA transaction repository and service that persist
  transactions and map them to API responses.
- Added `POST /api/transactions` to create a transaction and
  `GET /api/transactions` to list all transactions.
- Added the initial Flyway migration for financial transactions. The
  `transactions` table stores positive BRL amounts, transaction dates,
  descriptions, and either `INCOME` or `EXPENSE` types.
- Added a publishing job to the documentation automation workflow. When
  documentation changes are generated, the workflow applies the patch and
  pushes a documentation commit to `main`.
- Added a GitHub Actions job for automated project-documentation updates,
  using the project-documentation agent and Copilot CLI to prepare a patch
  from the push diff.
- Established the initial Spring Boot foundation with Java 21, Maven,
  PostgreSQL, JPA, Flyway, validation, Lombok, and REST dependencies.
- Added a root health endpoint that returns `Ok`.
- Added Docker Compose services for PostgreSQL 18 and the API, including
  database health checks and persistent database storage.
- Added CI and automated test-generation workflows for Java 21 Maven
  projects.