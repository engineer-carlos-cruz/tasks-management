# AGENTS.md — TaskFlow API

## Project state

Very early skeleton — only the main application class and a context-loads test exist. No controllers, services, repositories, entities, or security yet.

## Commands

```sh
./gradlew build       # full build
./gradlew bootRun     # start the app (default port 8080)
./gradlew test        # run all tests
```

## Key facts

- **Java 26** (toolchain in `build.gradle`)
- **Spring Boot 4.1.0**, Gradle 9.5.1 wrapper
- Package: `dev.ccruz.task_management` (underscore, not hyphen — hyphen is invalid in Java packages)
- Dependency names: `spring-boot-starter-webmvc` and `spring-boot-starter-webmvc-test` (note `webmvc`, not `web`)

## PRD vs. current reality

The `docs/PRD.md` references many technologies (Spring Security, JPA, PostgreSQL, Flyway, Docker, Lombok, MapStruct, OpenAPI, Testcontainers, etc.) that are **not yet declared** in `build.gradle`. The PRD also says "Maven" but the project is Gradle-based. Always consult `build.gradle` for the actual dependency state.

## Architecture (planned)

Standard layered architecture under `dev.ccruz.task_management`:

```
controller -> service -> repository -> domain
```

Additional layers: `dto`, `mapper`, `config`, `security`, `exception`, `validation`.

## Testing

Uses JUnit 5 + Spring Boot test slice annotations when implemented. No testcontainers or mockito dependencies yet.
