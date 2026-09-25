INSERT INTO users (name, email, password, user_role, is_active, creation_date) VALUES
    (
    'Administrador',
    'admin@barbearia.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP
    ),
    (
    'Barbeiro Master',
    'barbeiro@barbearia.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    'BARBER',
    TRUE,
    CURRENT_TIMESTAMP
    ),
    (
    'Cliente Teste',
    'cliente@gmail.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    'CUSTOMER',
    TRUE,
    CURRENT_TIMESTAMP
    );

-- default password: 123456

INSERT INTO barbers (user_id, specialty, rating, avatar)
SELECT user_id, 'Cortes Modernos, Degradê e Barboterapia', 5.0, ':)'
FROM users
WHERE email = 'barbeiro@barbearia.com';