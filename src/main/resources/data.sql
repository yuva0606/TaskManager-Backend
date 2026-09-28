INSERT INTO users (id, username, password) VALUES
(1, 'yuva', '$2a$10$ztCYKOR1x8iR2IJNL/Ub7uvXzfw13F/stt83VtD/s7y3ecfUo4fD6'),
(2, 'jas', '$2a$10$Y5N.GVqGHHIHsO6TptfQNef1VkQ6jlYVv2lx9BxMpYGZ1YjLNz1Le');

INSERT INTO projects (id, name, user_id) VALUES
(1, 'React Learning', 1),
(2, 'Java Microservices', 1),
(3, 'DSA Practice', 2);


-- Tasks
INSERT INTO task
    (id, title, description, status, priority, project_id, due_date)
VALUES
    (1, 'Learn React Context',
     'Understand Context and Provider properly',
     'done', 'high', 1, '2026-09-10'),

    (2, 'Learn React Router',
     'Understand dynamic routes and navigation',
     'done', 'medium', 1, '2026-09-12'),

    (3, 'Build Task API',
     'Create REST endpoints for tasks',
     'in-progress', 'high', 2, '2026-09-15'),

    (4, 'Learn Spring Data JPA',
     'Understand repositories and derived queries',
     'todo', 'high', 2, '2026-09-18'),

    (5, 'Practice Dynamic Programming',
     'Solve interval DP problems',
     'in-progress', 'medium', 3, '2026-09-20'),

    (6, 'Practice Graphs',
     'Solve BFS and DFS problems',
     'todo', 'low', 3, '2026-09-25');
