# Product Requirements Document (PRD)

# TaskFlow API

**Versión:** 1.0
**Estado:** Draft
**Tecnología objetivo:** Spring Boot

---

# 1. Resumen

TaskFlow API es una aplicación backend desarrollada con Spring Boot para gestionar proyectos y tareas de trabajo colaborativo.

El objetivo del proyecto es demostrar buenas prácticas de desarrollo backend mediante una API REST bien diseñada, segura, escalable y fácilmente extensible.

Aunque inicialmente será una aplicación pequeña, la arquitectura deberá permitir incorporar funcionalidades avanzadas como comentarios, notificaciones, mensajería interna, WebSockets, RabbitMQ y otros módulos sin necesidad de rediseñar la aplicación.

---

# 2. Objetivos

El proyecto debe demostrar conocimientos en:

* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* PostgreSQL
* Flyway
* Validaciones
* Arquitectura por capas
* Manejo de excepciones
* Documentación OpenAPI
* Docker
* Testing

---

# 3. Usuarios

## Usuario

Puede:

* Crear proyectos.
* Administrar sus tareas.
* Actualizar el estado de las tareas.
* Asignar tareas.
* Consultar sus proyectos.

---

## Administrador (futuro)

Puede:

* Administrar usuarios.
* Consultar estadísticas.
* Gestionar permisos.

---

# 4. Alcance de la primera versión (MVP)

## Autenticación

* Registro de usuario.
* Inicio de sesión.
* JWT.
* Refresh Token (opcional para MVP).
* Cambio de contraseña.

---

## Usuarios

Cada usuario tendrá:

* id
* nombre
* apellido
* email
* contraseña
* fecha de creación
* fecha de actualización
* estado

---

## Proyectos

Cada proyecto tendrá:

* id
* nombre
* descripción
* propietario
* fecha de creación
* fecha de actualización

Operaciones:

* Crear
* Editar
* Eliminar
* Consultar
* Listar

---

## Tareas

Cada tarea tendrá:

* id
* título
* descripción
* estado
* prioridad
* fecha límite
* proyecto
* creador
* responsable
* fecha de creación
* fecha de actualización

Operaciones:

* Crear
* Actualizar
* Eliminar
* Consultar
* Listar
* Buscar por estado
* Buscar por prioridad

---

# 5. Estados de una tarea

```text
TODO

IN_PROGRESS

DONE
```

---

# 6. Prioridades

```text
LOW

MEDIUM

HIGH
```

---

# 7. Requisitos funcionales

## RF-001

El usuario debe poder registrarse.

---

## RF-002

El usuario debe iniciar sesión mediante JWT.

---

## RF-003

El usuario autenticado podrá crear proyectos.

---

## RF-004

Cada proyecto podrá contener múltiples tareas.

---

## RF-005

Una tarea debe pertenecer únicamente a un proyecto.

---

## RF-006

El usuario podrá actualizar el estado de una tarea.

---

## RF-007

El usuario podrá asignar una tarea a otro usuario.

---

## RF-008

El usuario podrá consultar únicamente los proyectos donde participa.

---

## RF-009

El usuario podrá consultar únicamente las tareas autorizadas.

---

## RF-010

La API deberá validar todos los datos de entrada.

---

# 8. Requisitos no funcionales

## Seguridad

* JWT
* Passwords cifradas con BCrypt
* Endpoints protegidos
* CORS configurable

---

## Rendimiento

La API deberá responder normalmente en menos de 300 ms para operaciones CRUD simples sobre un volumen moderado de datos.

---

## Calidad

* Código limpio
* Arquitectura mantenible
* Cobertura de pruebas para la lógica crítica
* Convenciones REST

---

## Base de datos

PostgreSQL.

Migraciones mediante Flyway.

---

## Documentación

Swagger/OpenAPI.

---

# 9. Modelo de dominio

```text
User

Project

Task
```

Relaciones:

```text
User
 │
 ├──────────────┐
 │              │
 │              │
 ▼              ▼
Project       Task
                 ▲
                 │
           Assigned User
```

---

# 10. Endpoints iniciales

## Auth

```
POST /auth/register

POST /auth/login
```

---

## Users

```
GET /users/me

PUT /users/me
```

---

## Projects

```
GET /projects

GET /projects/{id}

POST /projects

PUT /projects/{id}

DELETE /projects/{id}
```

---

## Tasks

```
GET /tasks

GET /tasks/{id}

POST /tasks

PUT /tasks/{id}

DELETE /tasks/{id}
```

---

# 11. Validaciones

Ejemplos:

Proyecto

* nombre obligatorio
* máximo 100 caracteres

Tarea

* título obligatorio
* prioridad válida
* estado válido
* fecha límite opcional
* descripción máximo 2000 caracteres

Usuario

* email único
* contraseña mínima de 8 caracteres

---

# 12. Arquitectura

```text
Controller

↓

Service

↓

Repository

↓

Database
```

Capas adicionales:

```text
DTO

Mapper

Validation

Exception

Configuration

Security

Domain
```

---

# 13. Tecnologías

* Java 21+
* Spring Boot
* Spring Security
* Spring Data JPA
* PostgreSQL
* Flyway
* Maven
* Docker
* Docker Compose
* Lombok
* MapStruct
* Bean Validation
* OpenAPI
* JUnit 5
* Mockito
* Testcontainers

---

# 14. Roadmap

## Versión 1

* Usuarios
* Login
* JWT
* Proyectos
* Tareas

---

## Versión 2

Comentarios.

```text
Task

↓

Comment
```

---

## Versión 3

Etiquetas.

```text
Task

↓

Tag
```

---

## Versión 4

Archivos adjuntos.

---

## Versión 5

Historial de cambios.

```text
Task

↓

TaskHistory
```

---

## Versión 6

Notificaciones.

```text
Notification
```

---

## Versión 7

Mensajería interna.

```text
Conversation

↓

Message
```

---

## Versión 8

WebSockets.

Mensajes en tiempo real.

---

## Versión 9

RabbitMQ.

Mensajería asíncrona.

---

## Versión 10

Redis.

Caché.

---

## Versión 11

Roles.

```text
ADMIN

MANAGER

USER
```

---

## Versión 12

Dashboard.

Métricas del usuario.

---

## Versión 13

Búsqueda avanzada.

Filtros.

Paginación.

Ordenamiento.

---

## Versión 14

API pública documentada.

Versionado.

---

# 15. Criterios de éxito

El MVP se considerará completado cuando:

* El registro e inicio de sesión funcionen correctamente.
* La autenticación mediante JWT proteja los recursos.
* Se puedan crear y administrar proyectos.
* Se puedan crear y administrar tareas.
* Las validaciones funcionen correctamente.
* La documentación OpenAPI esté disponible.
* Existan pruebas para los componentes críticos.
* La aplicación pueda ejecutarse mediante Docker Compose.
