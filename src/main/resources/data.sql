-- Sample data for FinAccessCore
-- This file will be executed after schema.sql
-- Password for all users: "password123" (BCrypt encoded)

-- Insert admin user
INSERT IGNORE INTO users (id, username, email, password_hash, active, created_at) 
VALUES (1, 'admin', 'admin@finaccesscore.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE, NOW());

-- Insert analyst user
INSERT IGNORE INTO users (id, username, email, password_hash, active, created_at) 
VALUES (2, 'analyst', 'analyst@finaccesscore.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE, NOW());

-- Insert viewer user
INSERT IGNORE INTO users (id, username, email, password_hash, active, created_at) 
VALUES (3, 'viewer', 'viewer@finaccesscore.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE, NOW());

-- Assign roles
INSERT IGNORE INTO user_roles (user_id, roles) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO user_roles (user_id, roles) VALUES (2, 'ROLE_ANALYST');
INSERT IGNORE INTO user_roles (user_id, roles) VALUES (3, 'ROLE_VIEWER');

-- Sample financial records
INSERT IGNORE INTO financial_record (id, amount, type, category, date, notes, user_id, deleted, created_at) 
VALUES 
(1, 5000.00, 'INCOME', 'Salary', '2024-01-15', 'Monthly salary', 1, FALSE, NOW()),
(2, 1200.00, 'EXPENSE', 'Rent', '2024-01-05', 'Monthly rent payment', 1, FALSE, NOW()),
(3, 300.00, 'EXPENSE', 'Groceries', '2024-01-10', 'Weekly groceries', 1, FALSE, NOW()),
(4, 150.00, 'EXPENSE', 'Utilities', '2024-01-12', 'Electricity and water', 1, FALSE, NOW()),
(5, 500.00, 'INCOME', 'Freelance', '2024-01-20', 'Freelance project payment', 1, FALSE, NOW()),
(6, 200.00, 'EXPENSE', 'Entertainment', '2024-01-18', 'Movie and dinner', 1, FALSE, NOW()),
(7, 5000.00, 'INCOME', 'Salary', '2024-02-15', 'Monthly salary', 1, FALSE, NOW()),
(8, 1200.00, 'EXPENSE', 'Rent', '2024-02-05', 'Monthly rent payment', 1, FALSE, NOW()),
(9, 350.00, 'EXPENSE', 'Groceries', '2024-02-10', 'Weekly groceries', 1, FALSE, NOW()),
(10, 160.00, 'EXPENSE', 'Utilities', '2024-02-12', 'Electricity and water', 1, FALSE, NOW());
