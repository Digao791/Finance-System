---
name: test-generation
description: Adds Java test classes for changed or uncovered Spring Boot behavior.
---

# Test Generation Skill

## Scope

In `changes` mode, use `changed-files.txt` and the available Git diff to
identify affected behavior. In `missing` mode, identify coverage gaps
across the repository. In both modes, inspect existing tests first.

## Rules

1. Create only new `.java` files under `src/test/java/`.
2. Preserve all existing files, including existing tests, production code,
   database migrations, and build configuration.
3. Prioritize business logic and service classes. Cover relevant success
   and failure scenarios without duplicating existing assertions.
4. Follow the project's JUnit and Spring testing conventions. Use Mockito
   when dependencies need isolation and a Spring test context when the
   behavior under test requires it.
5. Keep tests independent of execution order.
6. When a test class already exists, choose a distinct, descriptive name
   ending in `Test`, such as `TicketServiceDetailMappingTest`.
7. Leave Maven execution and Git branch operations to the workflow.

## Expected output

- New Java test classes under `src/test/java/`, when coverage is needed.
- A short summary of the scenarios added.
- Any verification limitations, with Maven execution marked as pending.