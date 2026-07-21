-- =====================================================================
-- Seed Data — AI-Powered Digital Twin & Interview Readiness Platform
-- NOTE: password_hash values below are BCrypt hashes of "Password@123"
-- =====================================================================

USE digital_twin_platform;

-- -----------------------------------------------------------------
-- Roles
-- -----------------------------------------------------------------
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Platform administrator with full access'),
('ROLE_STUDENT', 'Student user with access to learning & interview features');

-- -----------------------------------------------------------------
-- Users (1 admin, 3 students)
-- -----------------------------------------------------------------
INSERT INTO users (full_name, email, password_hash, is_email_verified, is_active) VALUES
('Platform Admin', 'admin@digitaltwin.io', '$2a$10$vwlcgZ7f852/g6hTt/LobeAjoGB.kiYQKqM.rGlkuK5.tGiauSpvu', TRUE, TRUE),
('Ananya Rao', 'ananya.rao@example.com', '$2a$10$vwlcgZ7f852/g6hTt/LobeAjoGB.kiYQKqM.rGlkuK5.tGiauSpvu', TRUE, TRUE),
('Rahul Mehta', 'rahul.mehta@example.com', '$2a$10$vwlcgZ7f852/g6hTt/LobeAjoGB.kiYQKqM.rGlkuK5.tGiauSpvu', TRUE, TRUE),
('Sneha Iyer', 'sneha.iyer@example.com', '$2a$10$vwlcgZ7f852/g6hTt/LobeAjoGB.kiYQKqM.rGlkuK5.tGiauSpvu', TRUE, TRUE);

INSERT INTO user_roles (user_id, role_id) VALUES
(1, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')),
(2, (SELECT id FROM roles WHERE name = 'ROLE_STUDENT')),
(3, (SELECT id FROM roles WHERE name = 'ROLE_STUDENT')),
(4, (SELECT id FROM roles WHERE name = 'ROLE_STUDENT'));

-- -----------------------------------------------------------------
-- Students
-- -----------------------------------------------------------------
INSERT INTO students (user_id, college_name, degree, branch, graduation_year, target_role, bio) VALUES
(2, 'PSG College of Technology', 'B.E.', 'Computer Science', 2026, 'Backend Developer', 'Aspiring backend engineer passionate about distributed systems.'),
(3, 'Coimbatore Institute of Technology', 'B.Tech', 'Information Technology', 2026, 'Full Stack Developer', 'Full stack enthusiast building side projects with React and Spring Boot.'),
(4, 'Anna University', 'B.E.', 'Electronics & Communication', 2025, 'Data Analyst', 'Transitioning from ECE to data analytics with a focus on Python and SQL.');

-- -----------------------------------------------------------------
-- Skills master
-- -----------------------------------------------------------------
INSERT INTO skills (name, category) VALUES
('Java', 'Programming'),
('Spring Boot', 'Framework'),
('React', 'Framework'),
('JavaScript', 'Programming'),
('Python', 'Programming'),
('SQL', 'Database'),
('MySQL', 'Database'),
('Data Structures & Algorithms', 'Core CS'),
('System Design', 'Core CS'),
('Communication', 'Soft Skill'),
('Problem Solving', 'Soft Skill'),
('Git', 'Tool'),
('Docker', 'Tool'),
('REST APIs', 'Core CS'),
('Machine Learning', 'Programming');

-- -----------------------------------------------------------------
-- Student Skills
-- -----------------------------------------------------------------
INSERT INTO student_skills (student_id, skill_id, proficiency, years_experience) VALUES
(1, (SELECT id FROM skills WHERE name='Java'), 'ADVANCED', 2.0),
(1, (SELECT id FROM skills WHERE name='Spring Boot'), 'INTERMEDIATE', 1.0),
(1, (SELECT id FROM skills WHERE name='SQL'), 'INTERMEDIATE', 1.5),
(2, (SELECT id FROM skills WHERE name='React'), 'ADVANCED', 1.5),
(2, (SELECT id FROM skills WHERE name='JavaScript'), 'ADVANCED', 2.0),
(2, (SELECT id FROM skills WHERE name='Java'), 'BEGINNER', 0.5),
(3, (SELECT id FROM skills WHERE name='Python'), 'INTERMEDIATE', 1.0),
(3, (SELECT id FROM skills WHERE name='SQL'), 'BEGINNER', 0.5);

-- -----------------------------------------------------------------
-- Sample Question Bank
-- -----------------------------------------------------------------
INSERT INTO question_bank (question_text, category, role, difficulty, is_ai_generated) VALUES
('Explain the difference between an abstract class and an interface in Java.', 'Technical', 'Backend Developer', 'EASY', FALSE),
('How would you design a URL shortening service?', 'Technical', 'Backend Developer', 'HARD', FALSE),
('What is the virtual DOM in React and why is it useful?', 'Technical', 'Full Stack Developer', 'MEDIUM', FALSE),
('Describe a time you had to work with a difficult teammate.', 'Behavioral', 'Full Stack Developer', 'EASY', FALSE),
('Explain normalization in relational databases with an example.', 'Technical', 'Data Analyst', 'MEDIUM', FALSE),
('Tell me about yourself.', 'HR', NULL, 'EASY', FALSE);

-- -----------------------------------------------------------------
-- AI Prompt Templates (admin-managed)
-- -----------------------------------------------------------------
INSERT INTO ai_prompts (prompt_key, prompt_template, description, updated_by) VALUES
('RESUME_ANALYSIS',
 'Analyze the following resume text and return a JSON object with fields: overall_score (0-100), ats_score (0-100), extracted_skills (array), strengths (array), weaknesses (array), suggestions (array), predicted_roles (array). Resume text: {{resume_text}}',
 'Prompt used to analyze an uploaded resume via Gemini', 'SYSTEM'),
('DIGITAL_TWIN_BEHAVIOR',
 'Based on the following student profile and activity data, infer a behavior_profile and learning_pattern as JSON. Profile: {{student_profile_json}}',
 'Prompt used to generate the digital twin behavioral model', 'SYSTEM'),
('INTERVIEW_QUESTION_GENERATION',
 'Generate {{count}} interview questions for the role of {{target_role}} at difficulty {{difficulty}}. Return as a JSON array of strings.',
 'Prompt used to generate mock interview questions', 'SYSTEM'),
('INTERVIEW_FEEDBACK',
 'Given the following interview question-answer pairs, evaluate the candidate and return JSON with confidence_score, communication_score, technical_score, strengths, weaknesses, detailed_feedback. Data: {{qa_pairs_json}}',
 'Prompt used to generate post-interview feedback', 'SYSTEM'),
('SKILL_GAP_ANALYSIS',
 'Compare the student''s current skills {{current_skills_json}} against the requirements for {{target_role}}. Return JSON with matched_skills, missing_skills, match_percentage.',
 'Prompt used for skill gap analysis', 'SYSTEM'),
('ROADMAP_GENERATION',
 'Based on the missing skills {{missing_skills_json}} for target role {{target_role}}, generate a learning roadmap as a JSON array of items with item_type (COURSE/PROJECT/CERTIFICATION/SKILL), title, description, resource_url.',
 'Prompt used to generate personalized learning roadmap', 'SYSTEM');
