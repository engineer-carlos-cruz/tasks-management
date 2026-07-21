# SPEC.md — TaskFlow API

> Especificación técnica formal (SDD).
> Versión 1.0 — MVP

---

## Índice

1. [Contrato OpenAPI](#1-contrato-openapi)
2. [Modelo de datos](#2-modelo-de-datos)
3. [Especificación de seguridad](#3-especificación-de-seguridad)
4. [Especificación de validaciones](#4-especificación-de-validaciones)
5. [Especificación de errores](#5-especificación-de-errores)
6. [Especificación de arquitectura](#6-especificación-de-arquitectura)
7. [Especificación de testing](#7-especificación-de-testing)
8. [Especificación de configuración](#8-especificación-de-configuración)

---

## 1. Contrato OpenAPI

```yaml
openapi: "3.1.0"
info:
  title: TaskFlow API
  version: "1.0.0"
  description: API para gestión de proyectos y tareas colaborativas

servers:
  - url: http://localhost:8080
    description: Local development

paths:
  # ─── Auth ──────────────────────────────────────────────────────
  /auth/register:
    post:
      tags: [Auth]
      summary: Registrar un nuevo usuario
      operationId: register
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegisterRequest'
      responses:
        '201':
          description: Usuario registrado exitosamente
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuthResponse'
        '400':
          $ref: '#/components/responses/ValidationError'
        '409':
          description: Email ya registrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /auth/login:
    post:
      tags: [Auth]
      summary: Iniciar sesión
      operationId: login
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginRequest'
      responses:
        '200':
          description: Inicio de sesión exitoso
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuthResponse'
        '401':
          description: Credenciales inválidas
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  # ─── Users ─────────────────────────────────────────────────────
  /users/me:
    get:
      tags: [Users]
      summary: Obtener perfil del usuario autenticado
      operationId: getCurrentUser
      security:
        - BearerAuth: []
      responses:
        '200':
          description: Perfil de usuario
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'

    put:
      tags: [Users]
      summary: Actualizar perfil del usuario autenticado
      operationId: updateCurrentUser
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateUserRequest'
      responses:
        '200':
          description: Perfil actualizado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
        '400':
          $ref: '#/components/responses/ValidationError'
        '401':
          $ref: '#/components/responses/Unauthorized'

  # ─── Projects ──────────────────────────────────────────────────
  /projects:
    get:
      tags: [Projects]
      summary: Listar proyectos del usuario autenticado
      operationId: listProjects
      security:
        - BearerAuth: []
      responses:
        '200':
          description: Lista de proyectos
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/ProjectResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'

    post:
      tags: [Projects]
      summary: Crear un nuevo proyecto
      operationId: createProject
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateProjectRequest'
      responses:
        '201':
          description: Proyecto creado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProjectResponse'
        '400':
          $ref: '#/components/responses/ValidationError'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /projects/{id}:
    get:
      tags: [Projects]
      summary: Obtener un proyecto por ID
      operationId: getProject
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Proyecto encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProjectResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'

    put:
      tags: [Projects]
      summary: Actualizar un proyecto
      operationId: updateProject
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateProjectRequest'
      responses:
        '200':
          description: Proyecto actualizado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProjectResponse'
        '400':
          $ref: '#/components/responses/ValidationError'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'

    delete:
      tags: [Projects]
      summary: Eliminar un proyecto
      operationId: deleteProject
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '204':
          description: Proyecto eliminado
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'

  # ─── Tasks ─────────────────────────────────────────────────────
  /tasks:
    get:
      tags: [Tasks]
      summary: Listar tareas del usuario autenticado
      operationId: listTasks
      security:
        - BearerAuth: []
      parameters:
        - name: status
          in: query
          required: false
          schema:
            $ref: '#/components/schemas/TaskStatus'
        - name: priority
          in: query
          required: false
          schema:
            $ref: '#/components/schemas/Priority'
        - name: projectId
          in: query
          required: false
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Lista de tareas
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/TaskResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'

    post:
      tags: [Tasks]
      summary: Crear una nueva tarea
      operationId: createTask
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateTaskRequest'
      responses:
        '201':
          description: Tarea creada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TaskResponse'
        '400':
          $ref: '#/components/responses/ValidationError'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          description: Proyecto no encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /tasks/{id}:
    get:
      tags: [Tasks]
      summary: Obtener una tarea por ID
      operationId: getTask
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Tarea encontrada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TaskResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'

    put:
      tags: [Tasks]
      summary: Actualizar una tarea
      operationId: updateTask
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateTaskRequest'
      responses:
        '200':
          description: Tarea actualizada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TaskResponse'
        '400':
          $ref: '#/components/responses/ValidationError'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'

    delete:
      tags: [Tasks]
      summary: Eliminar una tarea
      operationId: deleteTask
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '204':
          description: Tarea eliminada
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'

components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    # ─── Enums ───────────────────────────────────────────────────
    TaskStatus:
      type: string
      enum: [TODO, IN_PROGRESS, DONE]

    Priority:
      type: string
      enum: [LOW, MEDIUM, HIGH]

    # ─── Auth ────────────────────────────────────────────────────
    RegisterRequest:
      type: object
      required: [name, lastName, email, password]
      properties:
        name:
          type: string
          maxLength: 100
        lastName:
          type: string
          maxLength: 100
        email:
          type: string
          format: email
          maxLength: 255
        password:
          type: string
          minLength: 8
          maxLength: 100

    LoginRequest:
      type: object
      required: [email, password]
      properties:
        email:
          type: string
          format: email
        password:
          type: string

    AuthResponse:
      type: object
      properties:
        token:
          type: string
        email:
          type: string
        name:
          type: string

    # ─── User ────────────────────────────────────────────────────
    UserResponse:
      type: object
      properties:
        id:
          type: integer
          format: int64
        name:
          type: string
        lastName:
          type: string
        email:
          type: string
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time

    UpdateUserRequest:
      type: object
      properties:
        name:
          type: string
          maxLength: 100
        lastName:
          type: string
          maxLength: 100

    # ─── Project ─────────────────────────────────────────────────
    ProjectResponse:
      type: object
      properties:
        id:
          type: integer
          format: int64
        name:
          type: string
        description:
          type: string
        ownerId:
          type: integer
          format: int64
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time

    CreateProjectRequest:
      type: object
      required: [name]
      properties:
        name:
          type: string
          maxLength: 100
        description:
          type: string
          maxLength: 2000

    UpdateProjectRequest:
      type: object
      properties:
        name:
          type: string
          maxLength: 100
        description:
          type: string
          maxLength: 2000

    # ─── Task ────────────────────────────────────────────────────
    TaskResponse:
      type: object
      properties:
        id:
          type: integer
          format: int64
        title:
          type: string
        description:
          type: string
        status:
          $ref: '#/components/schemas/TaskStatus'
        priority:
          $ref: '#/components/schemas/Priority'
        dueDate:
          type: string
          format: date
          nullable: true
        projectId:
          type: integer
          format: int64
        creatorId:
          type: integer
          format: int64
        assigneeId:
          type: integer
          format: int64
          nullable: true
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time

    CreateTaskRequest:
      type: object
      required: [title, projectId]
      properties:
        title:
          type: string
          maxLength: 255
        description:
          type: string
          maxLength: 2000
        priority:
          $ref: '#/components/schemas/Priority'
          default: MEDIUM
        dueDate:
          type: string
          format: date
          nullable: true
        projectId:
          type: integer
          format: int64
        assigneeId:
          type: integer
          format: int64
          nullable: true

    UpdateTaskRequest:
      type: object
      properties:
        title:
          type: string
          maxLength: 255
        description:
          type: string
          maxLength: 2000
        status:
          $ref: '#/components/schemas/TaskStatus'
        priority:
          $ref: '#/components/schemas/Priority'
        dueDate:
          type: string
          format: date
          nullable: true
        assigneeId:
          type: integer
          format: int64
          nullable: true

    # ─── Error ───────────────────────────────────────────────────
    ErrorResponse:
      type: object
      properties:
        error:
          type: string
        message:
          type: string
        status:
          type: integer
        timestamp:
          type: string
          format: date-time
        path:
          type: string

    ValidationErrorDetail:
      type: object
      properties:
        field:
          type: string
        message:
          type: string
        rejectedValue:
          type: object
          nullable: true

    ValidationErrorResponse:
      allOf:
        - $ref: '#/components/schemas/ErrorResponse'
        - type: object
          properties:
            errors:
              type: array
              items:
                $ref: '#/components/schemas/ValidationErrorDetail'

  responses:
    Unauthorized:
      description: No autenticado
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
    Forbidden:
      description: No autorizado para este recurso
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
    NotFound:
      description: Recurso no encontrado
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
    ValidationError:
      description: Error de validación
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ValidationErrorResponse'
```

---

## 2. Modelo de datos

### 2.1 Entidades

#### `users`

| Columna       | Tipo         | Restricciones                        |
|---------------|--------------|--------------------------------------|
| id            | BIGSERIAL    | PK                                   |
| name          | VARCHAR(100) | NOT NULL                             |
| last_name     | VARCHAR(100) | NOT NULL                             |
| email         | VARCHAR(255) | NOT NULL, UNIQUE                     |
| password      | VARCHAR(255) | NOT NULL                             |
| enabled       | BOOLEAN      | NOT NULL, DEFAULT true               |
| created_at    | TIMESTAMP    | NOT NULL, DEFAULT now()              |
| updated_at    | TIMESTAMP    | NOT NULL, DEFAULT now()              |

#### `projects`

| Columna       | Tipo         | Restricciones                        |
|---------------|--------------|--------------------------------------|
| id            | BIGSERIAL    | PK                                   |
| name          | VARCHAR(100) | NOT NULL                             |
| description   | TEXT         | NULLABLE, max 2000 chars             |
| owner_id      | BIGINT       | NOT NULL, FK → users(id)             |
| created_at    | TIMESTAMP    | NOT NULL, DEFAULT now()              |
| updated_at    | TIMESTAMP    | NOT NULL, DEFAULT now()              |

#### `tasks`

| Columna       | Tipo         | Restricciones                        |
|---------------|--------------|--------------------------------------|
| id            | BIGSERIAL    | PK                                   |
| title         | VARCHAR(255) | NOT NULL                             |
| description   | TEXT         | NULLABLE, max 2000 chars             |
| status        | VARCHAR(20)  | NOT NULL, DEFAULT 'TODO'             |
| priority      | VARCHAR(10)  | NOT NULL, DEFAULT 'MEDIUM'           |
| due_date      | DATE         | NULLABLE                             |
| project_id    | BIGINT       | NOT NULL, FK → projects(id)          |
| creator_id    | BIGINT       | NOT NULL, FK → users(id)             |
| assignee_id   | BIGINT       | NULLABLE, FK → users(id)             |
| created_at    | TIMESTAMP    | NOT NULL, DEFAULT now()              |
| updated_at    | TIMESTAMP    | NOT NULL, DEFAULT now()              |

### 2.2 Relaciones

```
User ──1:N──► Project    (owner)
User ──1:N──► Task       (creator)
User ──1:N──► Task       (assignee)
Project ──1:N──► Task    (belongs to)
```

### 2.3 Índices

| Tabla      | Columnas             | Tipo   | Propósito                          |
|------------|----------------------|--------|-------------------------------------|
| users      | email                | UNIQUE | Búsqueda por email (login)         |
| projects   | owner_id             | BTREE  | Listar proyectos por usuario       |
| tasks      | project_id           | BTREE  | Listar tareas por proyecto         |
| tasks      | assignee_id          | BTREE  | Listar tareas asignadas            |
| tasks      | status               | BTREE  | Filtrar tareas por estado          |
| tasks      | priority             | BTREE  | Filtrar tareas por prioridad       |

### 2.4 Migraciones Flyway

| Archivo                   | Contenido                              |
|---------------------------|----------------------------------------|
| `V1__create_users.sql`    | Crear tabla `users` con índices        |
| `V2__create_projects.sql` | Crear tabla `projects` con FK e índice |
| `V3__create_tasks.sql`    | Crear tabla `tasks` con FKs e índices  |

> O, como alternativa, una sola migración `V1__init.sql` con todo el DDL.

---

## 3. Especificación de seguridad

### 3.1 Autenticación

| Propiedad           | Valor                            |
|---------------------|----------------------------------|
| Mecanismo           | JWT Bearer Token                 |
| Ubicación           | Header `Authorization: Bearer <token>` |
| Algoritmo JWT       | HMAC-SHA256 (HS256)              |
| Claims del token    | `sub` (user id), `email`, `iat`, `exp` |
| Expiración          | 24 horas                         |
| Refresh token       | NO (póst MVP, opcional)          |

### 3.2 Contraseñas

| Propiedad           | Valor                            |
|---------------------|----------------------------------|
| Algoritmo           | BCrypt                            |
| Strength            | 10 rounds                        |

### 3.3 Endpoints públicos vs. protegidos

| Endpoint                      | Acceso       |
|-------------------------------|--------------|
| `POST /auth/register`         | Público      |
| `POST /auth/login`            | Público      |
| `GET /users/me`               | Autenticado  |
| `PUT /users/me`               | Autenticado  |
| `GET /projects`               | Autenticado  |
| `GET /projects/{id}`          | Autenticado  |
| `POST /projects`              | Autenticado  |
| `PUT /projects/{id}`          | Autenticado  |
| `DELETE /projects/{id}`       | Autenticado  |
| `GET /tasks`                  | Autenticado  |
| `GET /tasks/{id}`             | Autenticado  |
| `POST /tasks`                 | Autenticado  |
| `PUT /tasks/{id}`             | Autenticado  |
| `DELETE /tasks/{id}`          | Autenticado  |

### 3.4 Reglas de autorización

| Recurso       | Regla                                                         |
|---------------|---------------------------------------------------------------|
| Project       | CRUD solo si el autenticado es el `owner_id`                  |
| Project       | GET si el autenticado es owner o tiene tareas en el proyecto  |
| Task          | GET solo si el autenticado es creator, assignee, o del proyecto|
| Task          | CRUD solo si el autenticado es creator o del proyecto owner   |
| User          | GET/PUT solo del propio usuario (`/users/me`)                 |

### 3.5 CORS

| Propiedad     | Valor por defecto       |
|---------------|--------------------------|
| Allowed origins | `http://localhost:3000` (configurable) |
| Allowed methods | GET, POST, PUT, DELETE, PATCH, OPTIONS |
| Allowed headers | Authorization, Content-Type |

---

## 4. Especificación de validaciones

### 4.1 Validaciones de entrada (Bean Validation)

#### `RegisterRequest`

| Campo     | Anotaciones                    | Mensaje                          |
|-----------|--------------------------------|----------------------------------|
| name      | `@NotBlank`, `@Size(max=100)`  | El nombre es obligatorio         |
| lastName  | `@NotBlank`, `@Size(max=100)`  | El apellido es obligatorio       |
| email     | `@NotBlank`, `@Email`, `@Size(max=255)` | Email inválido          |
| password  | `@NotBlank`, `@Size(min=8, max=100)` | Mínimo 8 caracteres       |

#### `LoginRequest`

| Campo     | Anotaciones                    |
|-----------|--------------------------------|
| email     | `@NotBlank`, `@Email`          |
| password  | `@NotBlank`                    |

#### `UpdateUserRequest`

| Campo    | Anotaciones                             |
|----------|-----------------------------------------|
| name     | `@Size(max=100)`                        |
| lastName | `@Size(max=100)`                        |

#### `CreateProjectRequest`

| Campo       | Anotaciones                    | Mensaje                          |
|-------------|--------------------------------|----------------------------------|
| name        | `@NotBlank`, `@Size(max=100)`  | El nombre del proyecto es obligatorio |
| description | `@Size(max=2000)`              | Máximo 2000 caracteres           |

#### `UpdateProjectRequest`

| Campo       | Anotaciones                    |
|-------------|--------------------------------|
| name        | `@Size(max=100)`               |
| description | `@Size(max=2000)`              |

#### `CreateTaskRequest`

| Campo       | Anotaciones                    | Mensaje                          |
|-------------|--------------------------------|----------------------------------|
| title       | `@NotBlank`, `@Size(max=255)`  | El título es obligatorio         |
| description | `@Size(max=2000)`              | Máximo 2000 caracteres           |
| priority    | `@NotNull`                     | Prioridad inválida               |
| projectId   | `@NotNull`                     | El proyecto es obligatorio       |

#### `UpdateTaskRequest`

| Campo       | Anotaciones                    |
|-------------|--------------------------------|
| title       | `@Size(max=255)`               |
| description | `@Size(max=2000)`              |
| priority    | Debe ser `LOW`, `MEDIUM`, `HIGH` |
| status      | Debe ser `TODO`, `IN_PROGRESS`, `DONE` |

### 4.2 Validaciones de negocio

| ID     | Regla                                                       | Código HTTP |
|--------|-------------------------------------------------------------|-------------|
| V-001  | El email debe ser único en el sistema                       | 409         |
| V-002  | El proyecto de una tarea debe existir                       | 404         |
| V-003  | El responsable asignado debe existir                        | 404         |
| V-004  | Solo el owner del proyecto puede modificarlo/eliminarlo     | 403         |
| V-005  | Solo el owner del proyecto puede eliminar tareas            | 403         |
| V-006  | La tarea debe pertenecer a un proyecto del usuario autenticado | 403      |

---

## 5. Especificación de errores

### 5.1 Estructura de respuesta de error

```json
{
  "error": "Bad Request",
  "message": "Mensaje descriptivo",
  "status": 400,
  "timestamp": "2026-07-20T10:30:00Z",
  "path": "/api/resource"
}
```

### 5.2 Errores de validación

```json
{
  "error": "Validation Failed",
  "message": "Errores de validación en los campos",
  "status": 400,
  "timestamp": "2026-07-20T10:30:00Z",
  "path": "/api/resource",
  "errors": [
    {
      "field": "name",
      "message": "El nombre es obligatorio",
      "rejectedValue": null
    }
  ]
}
```

### 5.3 Mapa de excepciones → HTTP

| Excepción                        | HTTP   | error              |
|----------------------------------|--------|---------------------|
| `MethodArgumentNotValidException`| 400    | Validation Failed   |
| `HttpMessageNotReadableException`| 400    | Bad Request         |
| `MissingServletRequestParameterException` | 400 | Bad Request  |
| `DuplicateEmailException`        | 409    | Conflict            |
| `ResourceNotFoundException`      | 404    | Not Found           |
| `UnauthorizedException`          | 401    | Unauthorized        |
| `ForbiddenException`             | 403    | Forbidden           |
| `AccessDeniedException`          | 403    | Forbidden           |
| `BadCredentialsException`        | 401    | Unauthorized        |
| `AuthenticationException`        | 401    | Unauthorized        |
| `GenericException`               | 500    | Internal Server Error|

### 5.4 Clases de excepción

| Clase                          | Extiende               |
|--------------------------------|------------------------|
| `DuplicateEmailException`      | `RuntimeException`     |
| `ResourceNotFoundException`    | `RuntimeException`     |
| `UnauthorizedException`        | `RuntimeException`     |
| `ForbiddenException`           | `RuntimeException`     |

---

## 6. Especificación de arquitectura

### 6.1 Paquetes

```
dev.ccruz.task_management
├── config/
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   └── WebConfig.java (CORS)
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ProjectController.java
│   └── TaskController.java
├── domain/
│   ├── User.java
│   ├── Project.java
│   ├── Task.java
│   ├── TaskStatus.java
│   └── Priority.java
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── UpdateUserRequest.java
│   │   ├── CreateProjectRequest.java
│   │   ├── UpdateProjectRequest.java
│   │   ├── CreateTaskRequest.java
│   │   └── UpdateTaskRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── ProjectResponse.java
│       ├── TaskResponse.java
│       ├── ErrorResponse.java
│       └── ValidationErrorResponse.java
├── exception/
│   ├── DuplicateEmailException.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   ├── ForbiddenException.java
│   └── GlobalExceptionHandler.java
├── mapper/
│   ├── UserMapper.java
│   ├── ProjectMapper.java
│   └── TaskMapper.java
├── repository/
│   ├── UserRepository.java
│   ├── ProjectRepository.java
│   └── TaskRepository.java
├── security/
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── ProjectService.java
│   └── TaskService.java
└── validation/
    └── (opcional — Bean Validation annotations cubren la mayoría)
```

### 6.2 Flujo de llamadas

```
HTTP Request
  → SecurityFilterChain (JwtAuthenticationFilter)
    → Controller
      → @Valid (Bean Validation)
        → Service (reglas de negocio + autorización)
          → Mapper (DTO ↔ Entity)
            → Repository (JPA)
              → Database
```

### 6.3 Dependencias entre capas

| Capa         | Depende de                              |
|--------------|------------------------------------------|
| controller   | service, dto                             |
| service      | repository, mapper, domain, security     |
| repository   | domain                                   |
| mapper       | domain, dto                              |
| security     | domain, repository                       |
| config       | security, controller                     |
| exception    | (ninguna, referenciada globalmente)      |

---

## 7. Especificación de testing

### 7.1 Estrategia

| Tipo de test               | Anotación          | Qué probar                          |
|----------------------------|---------------------|--------------------------------------|
| Unitario (servicios)       | `@ExtendWith(MockitoExtension.class)` | Lógica de negocio, reglas de autorización |
| Integración (controladores)| `@WebMvcTest`       | HTTP status, body, headers, validación |
| Integración (repositorios) | `@DataJpaTest` + Testcontainers | Operaciones CRUD, queries personalizadas |
| Integración (seguridad)    | `@SpringBootTest`   | Flujo completo register → login → access |
| Contexto                   | `@SpringBootTest`   | La aplicación arranca                 |

### 7.2 Cobertura mínima

| Componente       | Cobertura mínima esperada           |
|------------------|-------------------------------------|
| AuthService      | register exitoso, email duplicado, login exitoso, credenciales inválidas |
| ProjectService   | CRUD, autorización (owner vs no owner) |
| TaskService      | CRUD, filtros, asignación, autorización |
| AuthController   | 201 register, 200 login, 400/409 validaciones |
| ProjectController| 200/201/204/403/404 según caso       |
| TaskController   | 200/201/204/403/404 según caso       |
| Seguridad        | acceso sin token → 401, token inválido → 401 |

---

## 8. Especificación de configuración

### 8.1 Propiedades de aplicación (`application.yaml`)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskflow
    username: ${DB_USERNAME:taskflow}
    password: ${DB_PASSWORD:taskflow}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration

jwt:
  secret: ${JWT_SECRET:defaultSecretKeyThatMustBeChangedInProductionAtLeast256BitsLong}
  expiration-ms: 86400000

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### 8.2 Docker Compose

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://db:5432/taskflow
      DB_USERNAME: taskflow
      DB_PASSWORD: taskflow
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      db:
        condition: service_healthy

  db:
    image: postgres:17-alpine
    environment:
      POSTGRES_DB: taskflow
      POSTGRES_USER: taskflow
      POSTGRES_PASSWORD: taskflow
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U taskflow"]
      interval: 5s
      timeout: 3s
      retries: 5
```

---

## Apéndice: Mapa RF → Specs

| RF-ID | Especificación asociada |
|-------|-------------------------|
| RF-001 | `POST /auth/register` + validación email único |
| RF-002 | `POST /auth/login` + JWT en respuesta |
| RF-003 | `POST /projects` + Security Bearer |
| RF-004 | Modelo `Task.project_id` FK |
| RF-005 | Modelo `Task.project_id` NOT NULL |
| RF-006 | `PUT /tasks/{id}` campo `status` |
| RF-007 | `PUT /tasks/{id}` campo `assigneeId` |
| RF-008 | `GET /projects` filtrado por owner/participación |
| RF-009 | `GET /tasks` filtrado por usuario autenticado |
| RF-010 | Bean Validation en todos los DTOs de entrada |
