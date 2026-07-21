-- =====================================================================
-- AI-Powered Digital Twin & Interview Readiness Platform
-- Full MySQL Schema (DDL)
-- Engine: InnoDB | Charset: utf8mb4
-- =====================================================================

SET FOREIGN_KEY_CHECKS = 0;
CREATE DATABASE IF NOT EXISTS digital_twin_platform
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE digital_twin_platform;

-- =====================================================================
-- 1. AUTH & USERS
-- =====================================================================

CREATE TABLE roles (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50) NOT NULL UNIQUE,       -- ROLE_STUDENT, ROLE_ADMIN
    description     VARCHAR(255),
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE users (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    full_name               VARCHAR(150) NOT NULL,
    email                   VARCHAR(150) NOT NULL UNIQUE,
    password_hash           VARCHAR(255) NOT NULL,
    phone_number            VARCHAR(20),
    is_email_verified       BOOLEAN NOT NULL DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    email_verification_expiry DATETIME,
    reset_password_token    VARCHAR(255),
    reset_password_expiry   DATETIME,
    is_active               BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at           DATETIME,
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by              VARCHAR(100) DEFAULT 'SYSTEM',
    updated_by              VARCHAR(100) DEFAULT 'SYSTEM',
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    user_id     BIGINT UNSIGNED NOT NULL,
    role_id     BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
-- 2. STUDENT PROFILE
-- =====================================================================

CREATE TABLE students (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT UNSIGNED NOT NULL UNIQUE,
    date_of_birth       DATE,
    gender              VARCHAR(20),
    address             VARCHAR(255),
    city                VARCHAR(100),
    state               VARCHAR(100),
    country             VARCHAR(100),
    college_name        VARCHAR(200),
    degree              VARCHAR(150),
    branch              VARCHAR(150),
    graduation_year     YEAR,
    profile_picture_url VARCHAR(500),
    bio                 TEXT,
    linkedin_url        VARCHAR(255),
    github_url          VARCHAR(255),
    portfolio_url       VARCHAR(255),
    target_role         VARCHAR(150),
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE education (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED NOT NULL,
    institution_name VARCHAR(200) NOT NULL,
    qualification   VARCHAR(150) NOT NULL,
    field_of_study  VARCHAR(150),
    start_year      YEAR,
    end_year        YEAR,
    grade           VARCHAR(50),
    is_current      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_education_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_education_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE skills (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    category        VARCHAR(100),                 -- e.g. Programming, Soft Skill, Tool
    description     VARCHAR(255),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE student_skills (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED NOT NULL,
    skill_id        BIGINT UNSIGNED NOT NULL,
    proficiency     ENUM('BEGINNER','INTERMEDIATE','ADVANCED','EXPERT') NOT NULL DEFAULT 'BEGINNER',
    years_experience DECIMAL(3,1) DEFAULT 0.0,
    is_verified     BOOLEAN NOT NULL DEFAULT FALSE, -- verified via AI resume analysis
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_skills_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    UNIQUE KEY uq_student_skill (student_id, skill_id),
    INDEX idx_student_skills_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE projects (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED NOT NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    tech_stack      VARCHAR(500),
    project_url     VARCHAR(255),
    repo_url        VARCHAR(255),
    start_date      DATE,
    end_date        DATE,
    is_ongoing      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_projects_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_projects_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE certifications (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id          BIGINT UNSIGNED NOT NULL,
    title               VARCHAR(200) NOT NULL,
    issuing_organization VARCHAR(200),
    issue_date          DATE,
    expiry_date         DATE,
    credential_id       VARCHAR(150),
    credential_url      VARCHAR(255),
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_certifications_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_certifications_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE achievements (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED NOT NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    achievement_date DATE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_achievements_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_achievements_student (student_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 3. RESUME
-- =====================================================================

CREATE TABLE resumes (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    stored_path     VARCHAR(500) NOT NULL,
    file_type       VARCHAR(50),
    file_size_bytes BIGINT,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    uploaded_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resumes_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_resumes_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE resume_analysis (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    resume_id           BIGINT UNSIGNED NOT NULL,
    overall_score       DECIMAL(5,2),
    ats_score           DECIMAL(5,2),
    extracted_skills    JSON,
    strengths           JSON,
    weaknesses          JSON,
    suggestions         JSON,
    predicted_roles     JSON,
    raw_ai_response      LONGTEXT,
    analyzed_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_analysis_resume FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    INDEX idx_resume_analysis_resume (resume_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 4. DIGITAL TWIN
-- =====================================================================

CREATE TABLE digital_twins (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id              BIGINT UNSIGNED NOT NULL UNIQUE,
    behavior_profile        JSON,       -- AI-derived behavior traits
    learning_pattern        JSON,       -- AI-derived learning style
    career_prediction       JSON,
    confidence_index        DECIMAL(5,2),
    last_generated_at       DATETIME,
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_digital_twins_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
-- 5. INTERVIEW MODULE
-- =====================================================================

CREATE TABLE question_bank (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    question_text   TEXT NOT NULL,
    category        VARCHAR(100),          -- Technical, HR, Behavioral, DSA
    role            VARCHAR(150),          -- target job role
    difficulty      ENUM('EASY','MEDIUM','HARD') NOT NULL DEFAULT 'MEDIUM',
    is_ai_generated BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_bank_role (role),
    INDEX idx_question_bank_category (category)
) ENGINE=InnoDB;

CREATE TABLE interview_sessions (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED NOT NULL,
    target_role     VARCHAR(150),
    status          ENUM('IN_PROGRESS','COMPLETED','ABANDONED') NOT NULL DEFAULT 'IN_PROGRESS',
    started_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    DATETIME,
    overall_score   DECIMAL(5,2),
    CONSTRAINT fk_interview_sessions_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_interview_sessions_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE interview_questions (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    session_id          BIGINT UNSIGNED NOT NULL,
    question_bank_id    BIGINT UNSIGNED,       -- nullable if fully AI-generated ad hoc
    question_text       TEXT NOT NULL,
    sequence_no         INT NOT NULL,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_questions_session FOREIGN KEY (session_id) REFERENCES interview_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_questions_bank FOREIGN KEY (question_bank_id) REFERENCES question_bank(id) ON DELETE SET NULL,
    INDEX idx_interview_questions_session (session_id)
) ENGINE=InnoDB;

CREATE TABLE interview_answers (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    interview_question_id BIGINT UNSIGNED NOT NULL,
    answer_text         TEXT,
    answer_duration_seconds INT,
    submitted_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_answers_question FOREIGN KEY (interview_question_id) REFERENCES interview_questions(id) ON DELETE CASCADE,
    INDEX idx_interview_answers_question (interview_question_id)
) ENGINE=InnoDB;

CREATE TABLE interview_feedback (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    session_id           BIGINT UNSIGNED NOT NULL UNIQUE,
    confidence_score     DECIMAL(5,2),
    communication_score  DECIMAL(5,2),
    technical_score      DECIMAL(5,2),
    strengths            JSON,
    weaknesses           JSON,
    detailed_feedback    LONGTEXT,
    raw_ai_response       LONGTEXT,
    generated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_feedback_session FOREIGN KEY (session_id) REFERENCES interview_sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
-- 6. SKILL GAP, ROADMAP, ANALYTICS, NOTIFICATIONS
-- =====================================================================

CREATE TABLE skill_gap_analysis (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED NOT NULL,
    target_role     VARCHAR(150) NOT NULL,
    matched_skills  JSON,
    missing_skills  JSON,
    match_percentage DECIMAL(5,2),
    analyzed_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_skill_gap_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_skill_gap_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE roadmaps (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED NOT NULL,
    target_role     VARCHAR(150) NOT NULL,
    title           VARCHAR(200),
    description     TEXT,
    status          ENUM('ACTIVE','COMPLETED','ARCHIVED') NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_roadmaps_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_roadmaps_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE roadmap_items (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    roadmap_id      BIGINT UNSIGNED NOT NULL,
    item_type       ENUM('COURSE','PROJECT','CERTIFICATION','SKILL') NOT NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    resource_url    VARCHAR(255),
    sequence_no     INT NOT NULL DEFAULT 0,
    is_completed    BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at    DATETIME,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_roadmap_items_roadmap FOREIGN KEY (roadmap_id) REFERENCES roadmaps(id) ON DELETE CASCADE,
    INDEX idx_roadmap_items_roadmap (roadmap_id)
) ENGINE=InnoDB;

CREATE TABLE notifications (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT NOT NULL,
    type            VARCHAR(50) DEFAULT 'INFO',   -- INFO, WARNING, SUCCESS, ALERT
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id)
) ENGINE=InnoDB;

-- =====================================================================
-- 7. ADMIN: PROMPTS, REPORTS, AUDIT
-- =====================================================================

CREATE TABLE ai_prompts (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    prompt_key      VARCHAR(100) NOT NULL UNIQUE,   -- e.g. RESUME_ANALYSIS, INTERVIEW_FEEDBACK
    prompt_template LONGTEXT NOT NULL,
    description     VARCHAR(255),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE reports (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT UNSIGNED,
    report_type     VARCHAR(100) NOT NULL,     -- RESUME, INTERVIEW, PROGRESS
    file_path       VARCHAR(500),
    generated_by    BIGINT UNSIGNED,           -- admin/user id who triggered it
    generated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reports_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
    INDEX idx_reports_student (student_id)
) ENGINE=InnoDB;

CREATE TABLE audit_log (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED,
    action          VARCHAR(150) NOT NULL,
    entity_name     VARCHAR(100),
    entity_id       BIGINT UNSIGNED,
    details         JSON,
    ip_address      VARCHAR(50),
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_log_user (user_id),
    INDEX idx_audit_log_entity (entity_name, entity_id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
