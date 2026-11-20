---
name: project-documentation
description: Keeps project history, application documentation, development instructions, and README links up to date.
---

# Project documentation

Read `.documentation-input/push.diff` and `.documentation-input/commits.txt`.
Check the files at the current revision before documenting any change.
Review the whole repository, including backend, frontend, database,
tests, infrastructure, CI/CD, and documentation.

## Decide whether to update documentation

Review `.documentation-input/push.diff` before editing any file. Update
documentation only when the resulting repository state shows a meaningful
change that the current documentation does not already cover.

- Use the diff and the current implementation as evidence. A commit title,
  message, or stated intention alone is insufficient.
- If the diff is empty, leave all documentation files unchanged.
- Skip changes limited to formatting, whitespace, internal refactoring with
  unchanged behavior, and routine test changes that leave documented
  behavior and development procedures unchanged.
- For attempted fixes, document only the effect supported by the diff.
  If the intended behavior did not change, leave the docs unchanged.
  If only diagnostics changed, describe that change only when it matters
  to a documented workflow. Do not claim the original issue was resolved.
- Update only the sections affected by the verified change. An actual
  change to an endpoint, setup command, or CI behavior may qualify.
- If no update is warranted, print `No documentation changes required.`
  and finish without creating, editing, or reformatting any files.

## Project history

Maintain `docs/PROJECT-HISTORY.md` with `## YYYY-MM-DD` headings in
descending order. Use the UTC committer dates supplied by the workflow.
Summarize meaningful changes for each date in brief bullets that explain
what changed and why it matters. Consolidate related commits and append
new bullets to an existing date heading when appropriate.

Preserve historical entries. Skip duplicate bullets, merge-only entries,
and changes that only refresh these generated documents. On the first run,
document the supplied commit range and any earlier milestones supported by
repository evidence. Use commit dates for history, never the workflow run date.

## Application overview

Maintain `docs/APPLICATION-OVERVIEW.md`. Explain the application's purpose,
its users, the main workflow, and how its key components interact.
Describe features supported by the current implementation. Update this
document when application behavior or architecture changes.

## Development guide

Maintain `docs/DEVELOPMENT-GUIDE.md`. Document prerequisites, tool versions
declared by the project, environment variables, database setup, and commands
for starting, building, and testing the application. Explain the relevant
CI/CD tooling. Verify commands and settings against the Maven wrapper,
`pom.xml`, Compose files, and GitHub Actions workflows.
Use placeholders for credentials.

## README

Maintain a concise project summary, feature list, development tool list,
and repository structure in `README.md`. Update the relevant section when
the directory structure, tooling, or integrated features change.

Include this section once and verify that each relative link resolves.

## Documentation

- [Project history](docs/PROJECT-HISTORY.md)
- [Application overview](docs/APPLICATION-OVERVIEW.md)
- [Development guide](docs/DEVELOPMENT-GUIDE.md)