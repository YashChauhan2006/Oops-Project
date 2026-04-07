-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS ems_db;
USE ems_db;

-- Table for user data (Combined for Student, Teacher, Admin)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- SHA-256 Hashed
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL,
    academic_details TEXT, -- Used for students (e.g., Degree, Semester)
    profile_image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for exams
CREATE TABLE IF NOT EXISTS exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL DEFAULT 60,
    scheduled_at DATETIME,
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Table for MCQs
CREATE TABLE IF NOT EXISTS questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_option CHAR(1) NOT NULL, -- A, B, C, or D
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
);

-- Table for student results
CREATE TABLE IF NOT EXISTS results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    exam_id INT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    percentage DECIMAL(5,2),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
);

-- Insert a default Admin for initial login (Password: admin123)
-- (Note: In code, I'll hash it. For now, this is a placeholder or I'll provide hashed version)
-- Hashed 'admin123' (SHA-256): 240be518fabd2724ddb6f0403fed3d5d30bc342c2dd9475d564e59526aed42b9
INSERT INTO users (full_name, email, password, role) 
VALUES ('System Admin', 'admin@ems.com', '240be518fabd2724ddb6f0403fed3d5d30bc342c2dd9475d564e59526aed42b9', 'ADMIN');
