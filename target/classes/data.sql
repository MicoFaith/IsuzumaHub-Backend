-- Insert lab tests
INSERT INTO lab_tests (name, description, price, category, preparation_instructions, report_delivery_hours, sample_type, home_collection, created_at, updated_at, active)
VALUES
    ('Complete Blood Count (CBC)', 'Measures different components of blood including red cells, white cells, and platelets', 1500.00, 'Blood Test', 'Fast for 8 hours before the test', 24, 'Blood', TRUE, NOW(), NOW(), TRUE),
    ('Lipid Profile', 'Measures cholesterol and triglyceride levels in blood', 2000.00, 'Blood Test', 'Fast for 12 hours before the test', 48, 'Blood', TRUE, NOW(), NOW(), TRUE),
    ('HbA1c Test', 'Measures average blood sugar levels over the past 2-3 months', 1800.00, 'Diabetes', 'No special preparation required', 24, 'Blood', TRUE, NOW(), NOW(), TRUE),
    ('Thyroid Function Test', 'Measures thyroid hormone levels', 2500.00, 'Hormone Test', 'No special preparation required', 48, 'Blood', TRUE, NOW(), NOW(), TRUE),
    ('Urinalysis', 'Analyzes urine for various substances', 800.00, 'Urine Test', 'Collect first morning urine sample', 24, 'Urine', FALSE, NOW(), NOW(), TRUE);

-- Insert users (passwords are 'password123' encoded with BCrypt)
INSERT INTO users (username, password, email, role, created_at, updated_at)
VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', 'admin@example.com', 'ADMIN', NOW(), NOW()),
('employee1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', 'employee1@example.com', 'EMPLOYEE', NOW(), NOW()),
('employee2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', 'employee2@example.com', 'EMPLOYEE', NOW(), NOW()),
('patient1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', 'patient1@example.com', 'PATIENT', NOW(), NOW()),
('patient2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', 'patient2@example.com', 'PATIENT', NOW(), NOW());

-- Insert appointments
INSERT INTO appointments (appointment_number, patient_id, lab_test_id, employee_id, appointment_date, status, created_at, updated_at)
VALUES 
('APT001', 4, 1, 2, NOW() + INTERVAL '1 day', 'SCHEDULED', NOW(), NOW()),
('APT002', 4, 2, 2, NOW() + INTERVAL '2 days', 'SCHEDULED', NOW(), NOW()),
('APT003', 5, 3, 3, NOW() + INTERVAL '3 days', 'SCHEDULED', NOW(), NOW()),
('APT004', 5, 4, 3, NOW() - INTERVAL '1 day', 'COMPLETED', NOW(), NOW()),
('APT005', 4, 5, 2, NOW() - INTERVAL '2 days', 'CANCELLED', NOW(), NOW()); 