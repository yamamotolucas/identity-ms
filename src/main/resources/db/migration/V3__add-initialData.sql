INSERT INTO users (name, email, password, user_role, is_active, creation_date) VALUES
    (
    'Administrador',
    'admin@barbearia.com',
    '$2a$10$wK10RfqN6FwAovzV2ZJg2eUv7LkJWq5f5W6eXmP6c9z6u7w9jR5fW',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP
    ),
    (
    'Barbeiro Master',
    'barbeiro@barbearia.com',
    '$2a$10$wK10RfqN6FwAovzV2ZJg2eUv7LkJWq5f5W6eXmP6c9z6u7w9jR5fW',
    'BARBER',
    TRUE,
    CURRENT_TIMESTAMP
    ),
    (
    'Cliente Teste',
    'cliente@gmail.com',
    '$2a$10$wK10RfqN6FwAovzV2ZJg2eUv7LkJWq5f5W6eXmP6c9z6u7w9jR5fW',
    'CUSTOMER',
    TRUE,
    CURRENT_TIMESTAMP
    );