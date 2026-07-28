# AGENTS.md — TaskFlow API

## Commands

```sh
./gradlew build       # full build
./gradlew bootRun     # start the app (port 8080)
./gradlew test        # all tests
```

## Key facts

- **Java 26**, **Spring Boot 4.1.0**, Gradle 9.5.1 wrapper
- Package: `dev.ccruz.task_management` (underscore — hyphen invalid in Java packages)
- Dep names: `spring-boot-starter-webmvc` / `spring-boot-starter-webmvc-test` (not `web`)
- `settings.gradle` root name: `task-management`
- No Lombok, MapStruct, or `spring-boot-starter-validation` in `build.gradle` — write full getters/setters/constructors and manual mapping
- Password stored **in plain text** — no BCrypt or Spring Security wired yet
- `application.yaml` has only `spring.application.name` — no datasource/JPA/Flyway/JWT config yet
- `contextLoads` test **fails** without a datasource — expected until PostgreSQL is configured

## What exists vs what's planned

| Layer | Status |
|-------|--------|
| `domain/` | 3 entities + 2 enums — done |
| `repository/` | 3 repos with derived queries — done |
| `exception/` | 3 classes — done |
| `service/` | 4 services with business rules — done |
| `controller/`, `dto/`, `mapper/`, `security/`, `config/` | Not yet implemented |

## Docs

| File | Purpose |
|------|---------|
| `docs/SPEC.md` | Full SDD — API contract, data model, security, validation, error spec, package layout. Read before implementing. |
| `docs/PRD.md` | Product requirements — references technologies **not in `build.gradle`** and says "Maven". Always verify against `build.gradle`. |

## Architecture

```
controller -> service -> repository -> domain
```

Additional planned packages: `dto`, `mapper`, `config`, `security`, `exception`, `validation`.

## Service authorization rules (current)

- **Project**: CRUD only if authenticated user is the `owner`
- **Task view**: accessible if user is owner, creator, or assignee
- **Task edit**: only owner or creator
- **Task delete**: only project owner
- **AuthService.login**: plain-text password comparison (no BCrypt yet)
