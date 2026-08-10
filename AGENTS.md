# AGENTS.md — TaskFlow API

## Commands

```sh
./gradlew build                       # full build
./gradlew bootRun                     # start the app (port 8080)
./gradlew test                        # all tests
./gradlew test --tests *AuthServiceTest  # single test class
```

Runtime needs a reachable PostgreSQL (defaults `jdbc:postgresql://localhost:5432/taskflow`, user/pass `taskflow`, overridable via `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`).

## Key facts

- **Java 26**, **Spring Boot 4.1.0**, Gradle 9.5.1 wrapper
- Package: `dev.ccruz.task_management` (underscore — hyphen invalid in Java packages)
- Dep names: `spring-boot-starter-webmvc` / `spring-boot-starter-webmvc-test` (not `web`)
- `settings.gradle` root name: `task-management`
- No Lombok or MapStruct — write full getters/setters/constructors and manual mapping in `mapper/`
- `spring-boot-starter-validation` IS present — use Jakarta Validation annotations (AGENTS.md is kept in sync with build.gradle)
- **Spring Security + JWT wired**: only `/auth/register` and `/auth/login` are public; everything else requires a Bearer token. `BCryptPasswordEncoder` is the active encoder
- Flyway is enabled and runs migrations from `classpath:db/migration` on boot; JPA `ddl-auto: validate`. **Never edit an already-applied migration** — add a new `VN*` file
- JWT secret/expiry via `JWT_SECRET` (default is development-only) and `JWT_EXPIRATION_MS`
- `contextLoads` test requires the datasource to be reachable; it fails without PostgreSQL — expected

## Seed data gotcha

`V4__seed_data.sql` stores users' passwords **in plain text** (written before BCrypt was wired). Login via `/auth/login` now runs `BCryptPasswordEncoder.matches`, so seeded users cannot authenticate. Any work involving seeded logins needs a BCrypt-hashed password for those rows.

## What exists vs what's planned

| Layer | Status |
|-------|--------|
| `domain/` | 3 entities + 2 enums — done |
| `repository/` | 3 repos with derived queries — done |
| `exception/` | 5 classes + `GlobalExceptionHandler` — done |
| `service/` | 4 services with business rules — done |
| `controller/` | Auth, Project, Task, User — done |
| `dto/`, `mapper/` | request/response DTOs + mappers — done |
| `security/`, `config/` | JWT filter + provider, Security/Web config — done |
| Migrations | V1–V3 schemas, V4 seed — done |
| Tests | `TaskManagementApplicationTests` + security/service unit tests — partial |

## Docs

| File | Purpose |
|------|---------|
| `docs/SPEC.md` | Full SDD — API contract, data model, security, validation, error spec, package layout. Read before implementing. |
| `docs/PRD.md` | Product requirements — references technologies **not in `build.gradle`** and says "Maven". Verify claims against `build.gradle`. |

## Architecture

```
controller -> service -> repository -> domain
```

Services hold authorization logic. No authorization inside `repository/` or `controller/`.

## Service authorization rules (current)

- **Project**: CRUD only if authenticated user is the `owner`
- **Task view**: accessible if user is owner, creator, or assignee
- **Task edit**: only owner or creator
- **Task delete**: only project owner
- **AuthService.login**: BCrypt `passwordEncoder.matches` (not plain-text comparison)