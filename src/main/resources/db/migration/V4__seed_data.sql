INSERT INTO users (name, last_name, email, password, enabled)
VALUES ('Carlos', 'Admin', 'admin@taskflow.dev', 'admin123', TRUE),
       ('Carlos', 'Dev', 'carlos@taskflow.dev', 'password123', TRUE),
       ('Ana', 'PM', 'ana@taskflow.dev', 'password123', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO projects (name, description, owner_id)
VALUES ('Lanzamiento Web', 'Coordinacion del lanzamiento del sitio web', (SELECT id FROM users WHERE email = 'admin@taskflow.dev')),
       ('App Movil', 'Desarrollo de la aplicacion movil', (SELECT id FROM users WHERE email = 'carlos@taskflow.dev'));

INSERT INTO tasks (title, description, status, priority, due_date, project_id, creator_id, assignee_id)
VALUES ('Disenar landing', 'Crear el diseno de la landing page', 'IN_PROGRESS', 'HIGH', CURRENT_DATE + 7,
        (SELECT id FROM projects WHERE name = 'Lanzamiento Web'),
        (SELECT id FROM users WHERE email = 'admin@taskflow.dev'),
        (SELECT id FROM users WHERE email = 'ana@taskflow.dev')),
       ('Configurar CI/CD', 'Pipeline de integracion continua', 'TODO', 'MEDIUM', CURRENT_DATE + 14,
        (SELECT id FROM projects WHERE name = 'Lanzamiento Web'),
        (SELECT id FROM users WHERE email = 'admin@taskflow.dev'),
        (SELECT id FROM users WHERE email = 'admin@taskflow.dev')),
       ('Homepage responsive', 'Adaptar la homepage a moviles', 'DONE', 'MEDIUM', CURRENT_DATE - 3,
        (SELECT id FROM projects WHERE name = 'App Movil'),
        (SELECT id FROM users WHERE email = 'carlos@taskflow.dev'),
        (SELECT id FROM users WHERE email = 'carlos@taskflow.dev')),
       ('Push notifications', 'Integrar notificaciones push', 'IN_PROGRESS', 'LOW', CURRENT_DATE + 10,
        (SELECT id FROM projects WHERE name = 'App Movil'),
        (SELECT id FROM users WHERE email = 'carlos@taskflow.dev'),
        (SELECT id FROM users WHERE email = 'ana@taskflow.dev')),
       ('Pruebas de carga', 'Validar rendimiento bajo carga', 'TODO', 'HIGH', CURRENT_DATE + 21,
        (SELECT id FROM projects WHERE name = 'Lanzamiento Web'),
        (SELECT id FROM users WHERE email = 'admin@taskflow.dev'),
        (SELECT id FROM users WHERE email = 'carlos@taskflow.dev'));