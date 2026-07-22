# AGENTS.md — TaskFlow API

Very early skeleton — only `TaskManagementApplication.java` and its context-loads test exist. No controllers, services, repositories, entities, or security.

## Commands

```sh
./gradlew build       # full build
./gradlew bootRun     # start the app (port 8080)
./gradlew test        # all tests
```

## Key facts

- **Java 26** (toolchain in `build.gradle`), **Spring Boot 4.1.0**, Gradle 9.5.1 wrapper
- Package: `dev.ccruz.task_management` (underscore — hyphen is invalid in Java packages)
- Dep names: `spring-boot-starter-webmvc` / `spring-boot-starter-webmvc-test` (note `webmvc`, not `web`)
- `settings.gradle` root name: `task-management`
- `src/main/resources/application.yaml` has only `spring.application.name` — no datasource, JPA, or security config yet

## Docs

| File | Purpose |
|------|---------|
| `docs/PRD.md` | Product requirements — references many technologies **not yet in `build.gradle`** (JPA, PostgreSQL, Flyway, Docker, Lombok, MapStruct, OpenAPI, Testcontainers, etc.) and says "Maven". Always verify against `build.gradle`. |
| `docs/SPEC.md` | Full technical SDD — API contract, data model, security, validation, error spec, package layout. Read this before implementing. |

## Architecture (planned)

```
controller -> service -> repository -> domain
```

Additional packages: `dto`, `mapper`, `config`, `security`, `exception`, `validation`.

## Testing

Uses JUnit 5 platform. No Mockito or Testcontainers in `build.gradle` yet.
