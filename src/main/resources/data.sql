-- Insert lab tests
INSERT INTO lab_tests (name, description, price, category, preparation_instructions, report_delivery_hours, sample_type, home_collection, created_at, updated_at, active)
VALUES
    ('Complete Blood Count (CBC)', 'Measures different components of blood including red cells, white cells, and platelets', 1500.00, 'Blood Test', 'Fast for 8 hours before the test', 24, 'Blood', TRUE, NOW(), NOW(), TRUE),
    ('Lipid Profile', 'Measures cholesterol and triglyceride levels in blood', 2000.00, 'Blood Test', 'Fast for 12 hours before the test', 48, 'Blood', TRUE, NOW(), NOW(), TRUE),
    ('HbA1c Test', 'Measures average blood sugar levels over the past 2-3 months', 1800.00, 'Diabetes', 'No special preparation required', 24, 'Blood', TRUE, NOW(), NOW(), TRUE),
    ('Thyroid Function Test', 'Measures thyroid hormone levels', 2500.00, 'Hormone Test', 'No special preparation required', 48, 'Blood', TRUE, NOW(), NOW(), TRUE),
    ('Urinalysis', 'Analyzes urine for various substances', 800.00, 'Urine Test', 'Collect first morning urine sample', 24, 'Urine', FALSE, NOW(), NOW(), TRUE)
ON CONFLICT (name) DO NOTHING;

-- Insert users (passwords are 'password123' encoded with BCrypt)
INSERT INTO users (first_name, last_name, email, password, phone, role, email_verified, created_at, updated_at, active)
VALUES
    ('Admin', 'User', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', '+1234567890', 'ADMIN', true, NOW(), NOW(), true),
    ('Employee', 'One', 'employee1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', '+1234567891', 'LAB_EMPLOYEE', true, NOW(), NOW(), true),
    ('Employee', 'Two', 'employee2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', '+1234567892', 'LAB_EMPLOYEE', true, NOW(), NOW(), true),
    ('Patient', 'One', 'patient1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', '+1234567893', 'PATIENT', true, NOW(), NOW(), true),
    ('Patient', 'Two', 'patient2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8', '+1234567894', 'PATIENT', true, NOW(), NOW(), true)
ON CONFLICT (email) DO NOTHING;

-- Insert appointments
INSERT INTO appointments (appointment_number, patient_id, test_id, assigned_employee_id, appointment_date_time, status, amount, paid, created_at, updated_at)
VALUES
    ('APT001', 4, 1, 2, NOW() + INTERVAL '1 day', 'PENDING', 1500.00, false, NOW(), NOW()),
    ('APT002', 4, 2, 2, NOW() + INTERVAL '2 days', 'CONFIRMED', 2000.00, false, NOW(), NOW()),
    ('APT003', 5, 3, 3, NOW() + INTERVAL '3 days', 'IN_PROGRESS', 1800.00, false, NOW(), NOW()),
    ('APT004', 5, 4, 3, NOW() - INTERVAL '1 day', 'COMPLETED', 2500.00, true, NOW(), NOW()),
    ('APT005', 4, 5, 2, NOW() - INTERVAL '2 days', 'CANCELLED', 800.00, false, NOW(), NOW())
ON CONFLICT (appointment_number) DO NOTHING;