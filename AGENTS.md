# Repository Guidelines

## Project Structure & Module Organization

- Multi-module Maven build. Parent `pom.xml` aggregates five modules: `search`, `search-hibernate`,
  `search-jpa-hibernate`, `dao`, `dao-hibernate`.
- Each module follows standard layout: `src/main/java` for sources and `src/main/resources` for resources.
- Existing unit tests are concentrated in `search` and `search-hibernate`; add new tests in the module you change.

## Build, Test, and Development Commands

- `mvn clean verify` — build all modules and run any tests configured.
- `mvn clean verify -Dgpg.skip=true` — CI baseline build (avoids signing during quality checks).
- `mvn -pl dao -am package` — build a single module and its dependencies.
- `mvn -DskipTests package` — build without running tests.
- `mvn -pl search-hibernate -am test` — run tests for a specific module (if present).

## Coding Style & Naming Conventions

- Java 8 source/target (see parent `maven-compiler-plugin`).
- Follow standard Java conventions: classes `UpperCamelCase`, methods/fields `lowerCamelCase`, constants
  `UPPER_SNAKE_CASE`.
- Keep indentation consistent with existing files (tabs appear in POMs); preserve local style rather than reformatting.
- No formatting or lint tools are configured; avoid sweeping style changes in unrelated files.

## Testing Guidelines

- Active test suite exists for `search` and `search-hibernate`; no tests currently in `dao`, `dao-hibernate` and
  `search-jpa-hibernate`.
- Place tests alongside their module under `src/test/java` and mirror package structure.
- Name tests clearly (e.g., `GenericDaoTest`, `SearchCriteriaTest`).

## Commit & Pull Request Guidelines

- Commit history uses short, descriptive subjects (often Spanish or English, e.g., “Actualización de librerías”, “Bump
  hibernate-core ...”). Keep subjects concise and imperative.
- For PRs, include:
    - A brief summary of changes and affected modules.
    - Linked issues or rationale for dependency updates.
    - Notes on compatibility impacts (Hibernate 5, Java 8) and any new tests added.

## Configuration & Compatibility Notes

- This project modernizes Hibernate Generic DAO for Hibernate 5; avoid introducing dependencies incompatible with
  Hibernate 5 or Java 8.
- Dependency updates often happen per-module (e.g., `search-hibernate`, `search-jpa-hibernate`); keep versions aligned
  when applicable.
