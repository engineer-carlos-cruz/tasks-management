# Especificaciones funcionales detalladas — TaskFlow API

> Versión 1.0 — MVP
> Cada especificación funcional (RF) describe: criterios de aceptación, contrato de entrada/salida, reglas de negocio, validaciones, seguridad, escenarios de error y dependencias.

---

## RF-001: Registro de usuario

> **Relacionado con:** PRD §4 (MVP Autenticación), SPEC §1 (`POST /auth/register`), SPEC §4.1 (validaciones RegisterRequest), SPEC §3.3 (endpoint público)

### Descripción

El usuario debe poder registrarse en el sistema proporcionando su nombre, apellido, email y contraseña.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-001-01 | La petición con todos los campos válidos crea un usuario y retorna HTTP 201 con un JWT |
| CA-001-02 | La petición con email existente retorna HTTP 409 (Conflict) |
| CA-001-03 | La petición con email inválido retorna HTTP 400 (Validation Error) |
| CA-001-04 | La petición con contraseña menor a 8 caracteres retorna HTTP 400 |
| CA-001-05 | La petición sin nombre retorna HTTP 400 |
| CA-001-06 | La contraseña se almacena cifrada con BCrypt (nunca en texto plano) |
| CA-001-07 | El usuario se crea con estado `enabled = true` por defecto |

### Contrato

```
POST /auth/register
Content-Type: application/json

Request:
{
  "name":       "string (requerido, max 100)",
  "lastName":   "string (requerido, max 100)",
  "email":      "string (requerido, formato email, max 255)",
  "password":   "string (requerido, min 8, max 100)"
}

Response 201:
{
  "token":      "string (JWT)",
  "email":      "string",
  "name":       "string"
}

Response 400:
{
  "error":      "Validation Failed",
  "message":    "Errores de validación en los campos",
  "status":     400,
  "timestamp":  "2026-07-20T10:30:00Z",
  "path":       "/auth/register",
  "errors":     [ { "field": "email", "message": "Email inválido", "rejectedValue": "..." } ]
}

Response 409:
{
  "error":      "Conflict",
  "message":    "El email ya está registrado",
  "status":     409,
  "timestamp":  "2026-07-20T10:30:00Z",
  "path":       "/auth/register"
}
```

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-001-01 | El email debe ser único en el sistema (validación en service, no solo en DB) |
| RN-001-02 | El usuario se crea con `enabled = true` |
| RN-001-03 | Se genera un JWT inmediatamente después del registro y se devuelve en la respuesta |

### Validaciones

| Campo | Regla | Código |
|-------|-------|--------|
| name | `@NotBlank`, `@Size(max=100)` | 400 |
| lastName | `@NotBlank`, `@Size(max=100)` | 400 |
| email | `@NotBlank`, `@Email`, `@Size(max=255)` | 400 |
| password | `@NotBlank`, `@Size(min=8, max=100)` | 400 |
| email único | `UserRepository.existsByEmail()` | 409 |

### Seguridad

- Endpoint **público** (no requiere JWT)

### Dependencias

- `User` entity, `UserRepository`, `AuthService`, `AuthController`, `JwtTokenProvider`, `SecurityConfig`

---

## RF-002: Inicio de sesión con JWT

> **Relacionado con:** PRD §4 (MVP Autenticación), SPEC §1 (`POST /auth/login`), SPEC §3.1 (JWT)

### Descripción

El usuario debe iniciar sesión proporcionando email y contraseña, y recibir un JWT para autenticar peticiones posteriores.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-002-01 | Credenciales válidas retornan HTTP 200 con JWT |
| CA-002-02 | Email inexistente retorna HTTP 401 |
| CA-002-03 | Contraseña incorrecta retorna HTTP 401 |
| CA-002-04 | El JWT contiene los claims `sub` (user id), `email`, `iat`, `exp` |
| CA-002-05 | El JWT expira en 24 horas |

### Contrato

```
POST /auth/login
Content-Type: application/json

Request:
{
  "email":      "string (requerido, formato email)",
  "password":   "string (requerido)"
}

Response 200:
{
  "token":      "string (JWT)",
  "email":      "string",
  "name":       "string"
}

Response 401:
{
  "error":      "Unauthorized",
  "message":    "Credenciales inválidas",
  "status":     401,
  "timestamp":  "2026-07-20T10:30:00Z",
  "path":       "/auth/login"
}
```

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-002-01 | La contraseña se verifica contra el hash BCrypt almacenado |
| RN-002-02 | El JWT se genera con clave HMAC-SHA256 (HS256) |
| RN-002-03 | El JWT tiene expiración de 86400000 ms (24 horas) |

### Validaciones

| Campo | Regla | Código |
|-------|-------|--------|
| email | `@NotBlank`, `@Email` | 400 |
| password | `@NotBlank` | 400 |

### Seguridad

- Endpoint **público** (no requiere JWT)

### Dependencias

- RF-001 (necesita usuarios registrados para probar)
- `AuthService`, `AuthController`, `JwtTokenProvider`, `SecurityConfig`, `UserRepository`

---

## RF-003: El usuario autenticado podrá crear proyectos

> **Relacionado con:** PRD §4 (MVP Proyectos), SPEC §1 (`POST /projects`), SPEC §3.3 (endpoint protegido), SPEC §4.1 (validaciones CreateProjectRequest)

### Descripción

Un usuario autenticado puede crear un proyecto proporcionando nombre y descripción opcional. El usuario autenticado se convierte en el propietario del proyecto.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-003-01 | Usuario autenticado crea proyecto → HTTP 201 |
| CA-003-02 | Proyecto sin nombre → HTTP 400 |
| CA-003-03 | Proyecto con nombre > 100 caracteres → HTTP 400 |
| CA-003-04 | El `ownerId` del proyecto es el ID del usuario autenticado |
| CA-003-05 | Usuario sin token → HTTP 401 |

### Contrato

```
POST /projects
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "name":        "string (requerido, max 100)",
  "description": "string (opcional, max 2000)"
}

Response 201:
{
  "id":           "integer",
  "name":         "string",
  "description":  "string",
  "ownerId":      "integer",
  "createdAt":    "datetime",
  "updatedAt":    "datetime"
}

Response 400:
{
  "error":  "Validation Failed",
  "status": 400,
  "errors": [ { "field": "name", "message": "El nombre del proyecto es obligatorio" } ]
}

Response 401:
{
  "error":  "Unauthorized",
  "status": 401
}
```

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-003-01 | El propietario del proyecto es siempre el usuario autenticado que realiza la petición |
| RN-003-02 | La descripción es opcional |

### Validaciones

| Campo | Regla | Código |
|-------|-------|--------|
| name | `@NotBlank`, `@Size(max=100)` | 400 |
| description | `@Size(max=2000)` | 400 |

### Seguridad

- Endpoint **protegido** (requiere JWT)

### Dependencias

- RF-002 (necesita JWT para autenticación)
- `Project` entity, `ProjectRepository`, `ProjectMapper`, `ProjectService`, `ProjectController`, `SecurityConfig`

---

## RF-004: Cada proyecto podrá contener múltiples tareas

> **Relacionado con:** PRD §4 (MVP Tareas), SPEC §2.1 (modelo `tasks.project_id`)

### Descripción

Un proyecto puede tener asociadas múltiples tareas. Esta es una relación `1:N` reflejada en el modelo de datos.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-004-01 | Un proyecto puede tener 0 o más tareas asociadas |
| CA-004-02 | Al eliminar un proyecto, se eliminan todas sus tareas en cascada |

### Contrato

Este RF no expone un endpoint propio. Es una restricción del modelo de datos.

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-004-01 | `Task.project_id` es FK NOT NULL hacia `projects.id` |
| RN-004-02 | DELETE CASCADE: al borrar un proyecto se borran sus tareas |

### Validaciones

- Se valida en RF-005

### Seguridad

- Se valida en RF-009

### Dependencias

- RF-003 (necesita proyectos)
- RF-005 (task pertenece a un proyecto)

---

## RF-005: Una tarea debe pertenecer únicamente a un proyecto

> **Relacionado con:** PRD §4 (MVP Tareas), SPEC §2.1 (modelo `tasks.project_id` NOT NULL)

### Descripción

Cada tarea pertenece exactamente a un proyecto. No existen tareas huérfanas ni tareas compartidas entre proyectos.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-005-01 | Crear tarea sin `projectId` → HTTP 400 |
| CA-005-02 | Crear tarea con `projectId` inexistente → HTTP 404 |
| CA-005-03 | La tarea creada tiene el `projectId` especificado |
| CA-005-04 | No se puede migrar una tarea a un proyecto inexistente |

### Contrato

Validación incluida en `POST /tasks` (ver RF-004 y RF-006 para contrato completo).

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-005-01 | `Task.project_id` es NOT NULL |
| RN-005-02 | El projecto referenciado debe existir al crear o actualizar la tarea |

### Validaciones

| Validación | Regla | Código |
|------------|-------|--------|
| projectId nulo | `@NotNull` | 400 |
| projectId inexistente | `ProjectRepository.existsById()` | 404 |

### Dependencias

- RF-003 (proyectos)
- `Task` entity, `TaskRepository`

---

## RF-006: El usuario podrá actualizar el estado de una tarea

> **Relacionado con:** PRD §5 (Estados: TODO, IN_PROGRESS, DONE), SPEC §1 (`PUT /tasks/{id}`)

### Descripción

El usuario autenticado (autorizado) puede cambiar el estado de una tarea a `TODO`, `IN_PROGRESS` o `DONE`.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-006-01 | Actualizar estado a `IN_PROGRESS` → HTTP 200 con estado reflejado |
| CA-006-02 | Actualizar estado a `DONE` → HTTP 200 con estado reflejado |
| CA-006-03 | Estado inválido → HTTP 400 |
| CA-006-04 | Tarea inexistente → HTTP 404 |
| CA-006-05 | Usuario no autorizado (ni creator, ni owner del proyecto) → HTTP 403 |

### Contrato

```
PUT /tasks/{id}
Authorization: Bearer <token>
Content-Type: application/json

Request (parcial — solo status):
{
  "status": "IN_PROGRESS"
}

Response 200:
{
  "id":          "integer",
  "title":       "string",
  "description": "string",
  "status":      "IN_PROGRESS",
  "priority":    "MEDIUM",
  "dueDate":     "date|null",
  "projectId":   "integer",
  "creatorId":   "integer",
  "assigneeId":  "integer|null",
  "createdAt":   "datetime",
  "updatedAt":   "datetime"
}

Response 403:
{
  "error":   "Forbidden",
  "message": "No tienes permiso para modificar esta tarea",
  "status":  403
}

Response 404:
{
  "error":   "Not Found",
  "message": "Tarea no encontrada",
  "status":  404
}
```

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-006-01 | Solo el creador de la tarea o el propietario del proyecto puede cambiar el estado |
| RN-006-02 | El estado debe ser uno de: `TODO`, `IN_PROGRESS`, `DONE` |

### Validaciones

| Campo | Regla | Código |
|-------|-------|--------|
| status | Debe ser `TODO`, `IN_PROGRESS` o `DONE` | 400 |

### Seguridad

- Endpoint **protegido**
- Solo el `creator_id` de la tarea o el `owner_id` del proyecto pueden modificar

### Dependencias

- RF-004, RF-005 (tareas y proyectos existentes)
- `TaskService`, `TaskController`

---

## RF-007: El usuario podrá asignar una tarea a otro usuario

> **Relacionado con:** PRD §4 (MVP Tareas — asignar), SPEC §1 (`PUT /tasks/{id}` con `assigneeId`)

### Descripción

El usuario autenticado puede asignar una tarea a otro usuario registrado.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-007-01 | Asignar tarea a un usuario existente → HTTP 200, `assigneeId` actualizado |
| CA-007-02 | Asignar tarea a un usuario inexistente → HTTP 404 |
| CA-007-03 | Desasignar tarea (`assigneeId = null`) → HTTP 200 |
| CA-007-04 | Usuario no autorizado → HTTP 403 |

### Contrato

```
PUT /tasks/{id}
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "assigneeId": 12345
}

Response 200: (misma estructura de TaskResponse con assigneeId actualizado)
```

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-007-01 | Solo el creador de la tarea o el propietario del proyecto puede asignar |
| RN-007-02 | El usuario asignado debe existir en la base de datos |
| RN-007-03 | No hay restricción de auto-asignación |

### Validaciones

| Validación | Regla | Código |
|------------|-------|--------|
| assigneeId inexistente | `UserRepository.existsById()` | 404 |

### Seguridad

- Endpoint **protegido**
- Misma regla que RF-006 (creator u owner del proyecto)

### Dependencias

- RF-004, RF-005, RF-006

---

## RF-008: El usuario podrá consultar únicamente los proyectos donde participa

> **Relacionado con:** PRD §4 (MVP Proyectos — listar), SPEC §1 (`GET /projects`), SPEC §3.4 (autorización)

### Descripción

Al listar proyectos, el endpoint retorna solo aquellos donde el usuario autenticado es propietario o tiene tareas asignadas.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-008-01 | Usuario ve sus propios proyectos (owner) → HTTP 200 con esos proyectos |
| CA-008-02 | Usuario ve proyectos donde tiene tareas asignadas (sin ser owner) → HTTP 200 |
| CA-008-03 | Usuario NO ve proyectos donde no participa de ninguna forma |
| CA-008-04 | Usuario sin token → HTTP 401 |

### Contrato

```
GET /projects
Authorization: Bearer <token>

Response 200:
[
  {
    "id":          "integer",
    "name":        "string",
    "description": "string",
    "ownerId":     "integer",
    "createdAt":   "datetime",
    "updatedAt":   "datetime"
  }
]
```

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-008-01 | Un proyecto es visible si el usuario autenticado es `owner_id` O tiene tareas donde es `creator_id` o `assignee_id` |
| RN-008-02 | El filtro se aplica en la capa de servicio (no en la consulta SQL nativa) |

### Seguridad

- Endpoint **protegido**
- Filtrado por participación del usuario autenticado

### Dependencias

- RF-003 (proyectos), RF-004/RF-005 (tareas)
- `ProjectService`, `ProjectRepository`, `TaskRepository`

---

## RF-009: El usuario podrá consultar únicamente las tareas autorizadas

> **Relacionado con:** PRD §4 (MVP Tareas — listar), SPEC §1 (`GET /tasks`), SPEC §3.4 (autorización)

### Descripción

Al listar tareas, el endpoint retorna solo las tareas que el usuario autenticado tiene permiso de ver: creadas por él, asignadas a él, o pertenecientes a sus proyectos.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-009-01 | Usuario ve tareas donde es creator → HTTP 200 |
| CA-009-02 | Usuario ve tareas donde es assignee → HTTP 200 |
| CA-009-03 | Usuario ve tareas de proyectos donde es owner → HTTP 200 |
| CA-009-04 | Usuario NO ve tareas sin ninguna relación |
| CA-009-05 | Filtro por `status` funciona correctamente |
| CA-009-06 | Filtro por `priority` funciona correctamente |
| CA-009-07 | Filtro por `projectId` funciona correctamente |
| CA-009-08 | Usuario sin token → HTTP 401 |

### Contrato

```
GET /tasks
Authorization: Bearer <token>
Query params (opcionales):
  ?status=TODO
  &priority=HIGH
  &projectId=1

Response 200:
[
  {
    "id":          "integer",
    "title":       "string",
    "description": "string",
    "status":      "TODO",
    "priority":    "HIGH",
    "dueDate":     "2026-08-01",
    "projectId":   "integer",
    "creatorId":   "integer",
    "assigneeId":  "integer|null",
    "createdAt":   "datetime",
    "updatedAt":   "datetime"
  }
]
```

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-009-01 | El usuario ve tareas donde es creator, assignee, o el proyecto es owner |
| RN-009-02 | Los filtros `status`, `priority`, `projectId` son opcionales y combinables |
| RN-009-03 | Si se filtra por `projectId`, el proyecto debe estar entre los visibles por el usuario |

### Validaciones

| Parámetro | Regla | Código |
|-----------|-------|--------|
| status | Debe ser `TODO`, `IN_PROGRESS` o `DONE` si se envía | 400 |
| priority | Debe ser `LOW`, `MEDIUM` o `HIGH` si se envía | 400 |
| projectId | Debe ser un ID numérico positivo si se envía | 400 |

### Seguridad

- Endpoint **protegido**
- Filtrado automático por visibilidad del usuario

### Dependencias

- RF-004, RF-005, RF-006, RF-007
- `TaskService`, `TaskRepository`, `TaskController`

---

## RF-010: La API deberá validar todos los datos de entrada

> **Relacionado con:** PRD §7 (RF-010), SPEC §4 (Validaciones), SPEC §5 (Manejo de errores)

### Descripción

Todos los endpoints que reciben datos de entrada deben validarlos usando Bean Validation y devolver errores estructurados en caso de datos inválidos.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-010-01 | Campos obligatorios ausentes → HTTP 400 con `ValidationErrorResponse` |
| CA-010-02 | Campos con formato inválido → HTTP 400 |
| CA-010-03 | Campos que exceden longitud máxima → HTTP 400 |
| CA-010-04 | La respuesta de error incluye `errors` array con `field`, `message`, `rejectedValue` |
| CA-010-05 | Enums con valores inválidos → HTTP 400 |
| CA-010-06 | JSON mal formado → HTTP 400 |

### Contrato

Ver SPEC §5.1 y §5.2 para la estructura de respuesta de error.

### Validaciones

Aplican todas las validadciones definidas en SPEC §4.1, resumidas por DTO:

| DTO | Validaciones |
|-----|-------------|
| `RegisterRequest` | name @NotBlank @Size(max=100), lastName @NotBlank @Size(max=100), email @NotBlank @Email @Size(max=255), password @NotBlank @Size(min=8, max=100) |
| `LoginRequest` | email @NotBlank @Email, password @NotBlank |
| `UpdateUserRequest` | name @Size(max=100), lastName @Size(max=100) |
| `CreateProjectRequest` | name @NotBlank @Size(max=100), description @Size(max=2000) |
| `UpdateProjectRequest` | name @Size(max=100), description @Size(max=2000) |
| `CreateTaskRequest` | title @NotBlank @Size(max=255), description @Size(max=2000), projectId @NotNull |
| `UpdateTaskRequest` | title @Size(max=255), description @Size(max=2000), status (enum), priority (enum) |

### Reglas de negocio

| ID | Regla |
|----|-------|
| RN-010-01 | Usar `@Valid` en todos los `@RequestBody` de los controladores |
| RN-010-02 | `GlobalExceptionHandler` captura `MethodArgumentNotValidException` y devuelve `ValidationErrorResponse` |
| RN-010-03 | `HttpMessageNotReadableException` para JSON mal formado o enums inválidos → 400 |

### Seguridad

- No aplica (son validaciones de entrada)

### Dependencias

- Todos los RF anteriores (la validación es transversal)

---

## Apéndice: Dependencias entre RFs

```
RF-010 (validaciones — transversal, aplica a todos)

RF-001 (register) ──► RF-002 (login)
                        │
                        ▼
                     RF-003 (crear proyectos)
                        │
                        ▼
                  ┌─────┴─────┐
                  │           │
                  ▼           ▼
             RF-004 (1:N)  RF-005 (pertenencia)
                  │           │
                  └─────┬─────┘
                        ▼
                  RF-006 (status)
                  RF-007 (asignar)
                  RF-008 (listar proyectos)
                  RF-009 (listar tareas)
```

## Apéndice: Mapa de archivos por RF

| RF | Archivos que implementan la spec |
|----|-----------------------------------|
| RF-001 | `User`, `UserRepository`, `AuthService.register()`, `AuthController`, `JwtTokenProvider`, `RegisterRequest`, `AuthResponse` |
| RF-002 | `AuthService.login()`, `AuthController`, `LoginRequest`, `JwtTokenProvider` |
| RF-003 | `Project`, `ProjectRepository`, `ProjectService.create()`, `ProjectController`, `ProjectMapper`, `CreateProjectRequest`, `ProjectResponse` |
| RF-004 | Modelo de datos (`Project.tasks` relationship, cascade) |
| RF-005 | Modelo de datos (`Task.project` @ManyToOne, NOT NULL), `TaskService.create()` |
| RF-006 | `TaskService.updateStatus()`, `TaskController`, `UpdateTaskRequest` |
| RF-007 | `TaskService.assignTask()`, `TaskController` |
| RF-008 | `ProjectService.listByUserParticipation()`, `ProjectController` |
| RF-009 | `TaskService.listAuthorized()`, `TaskController` |
| RF-010 | `GlobalExceptionHandler`, `@Valid` en todos los controladores, anotaciones en DTOs |
