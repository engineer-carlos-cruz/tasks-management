# User Stories — TaskFlow API

> Versión 1.0 — MVP
> Formato: `Como [rol], quiero [acción] para [beneficio].`

---

## US-001: Registro de usuario

**Relacionado con:** RF-001, PRD §4

> **Como** visitante, **quiero** registrarme con nombre, apellido, email y contraseña **para** obtener una cuenta y acceder al sistema.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-001-01 | La petición con todos los campos válidos crea un usuario y retorna HTTP 201 con un JWT |
| CA-001-02 | La petición con email existente retorna HTTP 409 |
| CA-001-03 | La petición con email inválido retorna HTTP 400 |
| CA-001-04 | La petición con contraseña menor a 8 caracteres retorna HTTP 400 |
| CA-001-05 | La petición sin nombre retorna HTTP 400 |
| CA-001-06 | La contraseña se almacena cifrada con BCrypt |
| CA-001-07 | El usuario se crea con estado `enabled = true` por defecto |

---

## US-002: Inicio de sesión

**Relacionado con:** RF-002, PRD §4

> **Como** usuario registrado, **quiero** iniciar sesión con mi email y contraseña **para** obtener un JWT y autenticar mis peticiones.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-002-01 | Credenciales válidas retornan HTTP 200 con JWT |
| CA-002-02 | Email inexistente retorna HTTP 401 |
| CA-002-03 | Contraseña incorrecta retorna HTTP 401 |
| CA-002-04 | El JWT contiene los claims `sub`, `email`, `iat`, `exp` |
| CA-002-05 | El JWT expira en 24 horas |

---

## US-003: Ver perfil propio

**Relacionado con:** SPEC §1 (`GET /users/me`), PRD §4

> **Como** usuario autenticado, **quiero** consultar mi perfil **para** ver mis datos personales.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-003-01 | Petición con token válido retorna HTTP 200 con los datos del usuario |
| CA-003-02 | Petición sin token retorna HTTP 401 |
| CA-003-03 | Los datos retornados incluyen id, name, lastName, email, createdAt, updatedAt |

---

## US-004: Actualizar perfil propio

**Relacionado con:** SPEC §1 (`PUT /users/me`), PRD §4

> **Como** usuario autenticado, **quiero** actualizar mi nombre y/o apellido **para** mantener mis datos personales actualizados.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-004-01 | Petición con token válido y datos correctos retorna HTTP 200 con perfil actualizado |
| CA-004-02 | Nombre que excede 100 caracteres retorna HTTP 400 |
| CA-004-03 | Petición sin token retorna HTTP 401 |

---

## US-005: Crear proyecto

**Relacionado con:** RF-003, PRD §4

> **Como** usuario autenticado, **quiero** crear un proyecto con nombre y descripción opcional **para** organizar mis tareas en él.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-005-01 | Usuario autenticado crea proyecto → HTTP 201 |
| CA-005-02 | Proyecto sin nombre → HTTP 400 |
| CA-005-03 | Proyecto con nombre > 100 caracteres → HTTP 400 |
| CA-005-04 | El `ownerId` del proyecto es el ID del usuario autenticado |
| CA-005-05 | Usuario sin token → HTTP 401 |

---

## US-006: Listar proyectos visibles

**Relacionado con:** RF-008, PRD §4

> **Como** usuario autenticado, **quiero** listar los proyectos donde participo (como propietario o con tareas asignadas) **para** ver solo los proyectos que me interesan.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-006-01 | Usuario ve sus propios proyectos (owner) → HTTP 200 con esos proyectos |
| CA-006-02 | Usuario ve proyectos donde tiene tareas asignadas sin ser owner → HTTP 200 |
| CA-006-03 | Usuario NO ve proyectos donde no participa |
| CA-006-04 | Usuario sin token → HTTP 401 |

---

## US-007: Ver detalle de un proyecto

**Relacionado con:** SPEC §1 (`GET /projects/{id}`), PRD §4

> **Como** usuario autenticado, **quiero** ver el detalle de un proyecto específico **para** consultar su información completa.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-007-01 | Proyecto existente y visible → HTTP 200 con datos del proyecto |
| CA-007-02 | Proyecto inexistente → HTTP 404 |
| CA-007-03 | Proyecto no visible para el usuario → HTTP 403 |
| CA-007-04 | Usuario sin token → HTTP 401 |

---

## US-008: Actualizar un proyecto

**Relacionado con:** SPEC §1 (`PUT /projects/{id}`), PRD §4

> **Como** propietario de un proyecto, **quiero** actualizar su nombre y/o descripción **para** mantenerlo al día.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-008-01 | Propietario actualiza proyecto → HTTP 200 con datos actualizados |
| CA-008-02 | Usuario no propietario → HTTP 403 |
| CA-008-03 | Proyecto inexistente → HTTP 404 |
| CA-008-04 | Nombre > 100 caracteres → HTTP 400 |

---

## US-009: Eliminar un proyecto

**Relacionado con:** SPEC §1 (`DELETE /projects/{id}`), PRD §4

> **Como** propietario de un proyecto, **quiero** eliminarlo **para** deshacerme de proyectos que ya no necesito.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-009-01 | Propietario elimina proyecto → HTTP 204 |
| CA-009-02 | Las tareas del proyecto se eliminan en cascada |
| CA-009-03 | Usuario no propietario → HTTP 403 |
| CA-009-04 | Proyecto inexistente → HTTP 404 |

---

## US-010: Crear tarea en un proyecto

**Relacionado con:** RF-004, RF-005, PRD §4

> **Como** usuario autenticado, **quiero** crear una tarea dentro de un proyecto **para** desglosar el trabajo en unidades manejables.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-010-01 | Usuario autenticado crea tarea con título y projectId → HTTP 201 |
| CA-010-02 | Tarea sin título → HTTP 400 |
| CA-010-03 | Tarea sin projectId → HTTP 400 |
| CA-010-04 | Tarea con projectId inexistente → HTTP 404 |
| CA-010-05 | La tarea creada pertenece al proyecto especificado |
| CA-010-06 | Usuario sin token → HTTP 401 |

---

## US-011: Listar tareas autorizadas

**Relacionado con:** RF-009, PRD §4

> **Como** usuario autenticado, **quiero** listar las tareas que me son visibles (creadas por mí, asignadas a mí, o de mis proyectos) y filtrarlas por estado, prioridad o proyecto **para** encontrar rápidamente lo que busco.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-011-01 | Usuario ve tareas donde es creator → HTTP 200 |
| CA-011-02 | Usuario ve tareas donde es assignee → HTTP 200 |
| CA-011-03 | Usuario ve tareas de proyectos donde es owner → HTTP 200 |
| CA-011-04 | Usuario NO ve tareas sin ninguna relación |
| CA-011-05 | Filtro por `status` funciona |
| CA-011-06 | Filtro por `priority` funciona |
| CA-011-07 | Filtro por `projectId` funciona |
| CA-011-08 | Usuario sin token → HTTP 401 |

---

## US-012: Ver detalle de una tarea

**Relacionado con:** SPEC §1 (`GET /tasks/{id}`), PRD §4

> **Como** usuario autenticado, **quiero** ver el detalle de una tarea específica **para** consultar su información completa.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-012-01 | Tarea existente y visible → HTTP 200 |
| CA-012-02 | Tarea inexistente → HTTP 404 |
| CA-012-03 | Tarea no visible para el usuario → HTTP 403 |
| CA-012-04 | Usuario sin token → HTTP 401 |

---

## US-013: Actualizar estado de una tarea

**Relacionado con:** RF-006, PRD §5

> **Como** usuario autorizado (creador de la tarea o propietario del proyecto), **quiero** cambiar el estado de una tarea a `TODO`, `IN_PROGRESS` o `DONE` **para** reflejar su progreso.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-013-01 | Actualizar estado a `IN_PROGRESS` → HTTP 200 |
| CA-013-02 | Actualizar estado a `DONE` → HTTP 200 |
| CA-013-03 | Estado inválido → HTTP 400 |
| CA-013-04 | Tarea inexistente → HTTP 404 |
| CA-013-05 | Usuario no autorizado → HTTP 403 |

---

## US-014: Asignar tarea a un usuario

**Relacionado con:** RF-007, PRD §4

> **Como** usuario autorizado (creador de la tarea o propietario del proyecto), **quiero** asignar una tarea a otro usuario registrado **para** delegar el trabajo.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-014-01 | Asignar a usuario existente → HTTP 200, `assigneeId` actualizado |
| CA-014-02 | Asignar a usuario inexistente → HTTP 404 |
| CA-014-03 | Desasignar tarea (`assigneeId = null`) → HTTP 200 |
| CA-014-04 | Usuario no autorizado → HTTP 403 |

---

## US-015: Actualizar datos de una tarea

**Relacionado con:** SPEC §1 (`PUT /tasks/{id}`), PRD §4

> **Como** usuario autorizado, **quiero** actualizar el título, descripción, prioridad, fecha límite o responsable de una tarea **para** mantenerla actualizada.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-015-01 | Actualizar campos válidos → HTTP 200 con tarea actualizada |
| CA-015-02 | Título > 255 caracteres → HTTP 400 |
| CA-015-03 | Prioridad inválida → HTTP 400 |
| CA-015-04 | Tarea inexistente → HTTP 404 |
| CA-015-05 | Usuario no autorizado → HTTP 403 |

---

## US-016: Eliminar una tarea

**Relacionado con:** SPEC §1 (`DELETE /tasks/{id}`), PRD §4

> **Como** propietario del proyecto, **quiero** eliminar una tarea **para** depurar el proyecto de tareas obsoletas.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-016-01 | Propietario del proyecto elimina tarea → HTTP 204 |
| CA-016-02 | Usuario no autorizado → HTTP 403 |
| CA-016-03 | Tarea inexistente → HTTP 404 |

---

## US-017: Validación de datos de entrada

**Relacionado con:** RF-010, PRD §7, SPEC §4

> **Como** desarrollador que consume la API, **quiero** que todos los endpoints validen los datos de entrada y devuelvan errores estructurados **para** integrar la API de forma predecible.

### Criterios de aceptación

| ID | Criterio |
|----|----------|
| CA-017-01 | Campos obligatorios ausentes → HTTP 400 con `ValidationErrorResponse` |
| CA-017-02 | Campos con formato inválido → HTTP 400 |
| CA-017-03 | Campos que exceden longitud máxima → HTTP 400 |
| CA-017-04 | La respuesta incluye `errors` array con `field`, `message`, `rejectedValue` |
| CA-017-05 | Enums con valores inválidos → HTTP 400 |
| CA-017-06 | JSON mal formado → HTTP 400 |

---

## Apéndice: Trazabilidad RF ↔ US

| RF | User Story |
|----|------------|
| RF-001 | US-001 |
| RF-002 | US-002 |
| RF-003 | US-005 |
| RF-004 | US-010 |
| RF-005 | US-010 |
| RF-006 | US-013 |
| RF-007 | US-014 |
| RF-008 | US-006 |
| RF-009 | US-011 |
| RF-010 | US-017 |
| — | US-003 (GET /users/me) |
| — | US-004 (PUT /users/me) |
| — | US-007 (GET /projects/{id}) |
| — | US-008 (PUT /projects/{id}) |
| — | US-009 (DELETE /projects/{id}) |
| — | US-012 (GET /tasks/{id}) |
| — | US-015 (PUT /tasks/{id}) |
| — | US-016 (DELETE /tasks/{id}) |
