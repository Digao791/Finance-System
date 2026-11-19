# Project History

## 2026-11-19

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