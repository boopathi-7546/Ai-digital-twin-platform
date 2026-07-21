# AI-Powered Digital Twin & Interview Readiness Platform — File-by-File Build Plan

This plan breaks the platform into build phases. Each phase is a coherent, testable slice — we'll generate real working code for one phase at a time, in this order, rather than dumping every file at once (which produces inconsistent, broken code at this scale).

Legend: `[ ]` not started · we'll check items off as we build.

---

## PHASE 0 — Project Scaffolding & Database

### Repo structure
```
digital-twin-platform/
├── backend/                 (Spring Boot 3, Java 21, Maven)
├── frontend/                (React 19, Vite)
├── database/                (SQL schema, seed data, migrations)
├── docker/                  (Dockerfiles, nginx config)
├── docker-compose.yml
├── .github/workflows/       (CI/CD)
└── README.md
```

### Database (`database/`)
- [ ] `schema.sql` — full DDL: tables, PKs, FKs, indexes, constraints
- [ ] `seed_data.sql` — sample admin, students, skills, questions
- [ ] `migrations/V1__init.sql` (Flyway-style, if we use Flyway)

**Core tables:**
`users`, `roles`, `user_roles`, `students`, `education`, `skills`, `student_skills`, `projects`, `certifications`, `achievements`, `resumes`, `resume_analysis`, `digital_twins`, `interview_sessions`, `interview_questions`, `question_bank`, `interview_answers`, `interview_feedback`, `skill_gap_analysis`, `roadmaps`, `roadmap_items`, `notifications`, `ai_prompts`, `reports`, `audit_log`

---

## PHASE 1 — Backend Foundation + Auth Module

### Config & bootstrapping
- [ ] `pom.xml`
- [ ] `application.yml` / `application-dev.yml` / `application-prod.yml`
- [ ] `DigitalTwinPlatformApplication.java`

### `config/`
- [ ] `SecurityConfig.java`
- [ ] `CorsConfig.java`
- [ ] `SwaggerConfig.java` (OpenAPI docs)
- [ ] `WebConfig.java`
- [ ] `PasswordEncoderConfig.java`

### `security/`
- [ ] `JwtTokenProvider.java`
- [ ] `JwtAuthFilter.java`
- [ ] `JwtAuthEntryPoint.java`
- [ ] `CustomUserDetails.java`
- [ ] `CustomUserDetailsService.java`

### `entity/`
- [ ] `User.java`, `Role.java`

### `dto/auth/`
- [ ] `RegisterRequest.java`, `LoginRequest.java`, `LoginResponse.java`
- [ ] `ForgotPasswordRequest.java`, `ResetPasswordRequest.java`
- [ ] `EmailVerificationRequest.java`

### `repository/`
- [ ] `UserRepository.java`, `RoleRepository.java`

### `service/` + `serviceimpl/`
- [ ] `AuthService.java` / `AuthServiceImpl.java`
- [ ] `EmailService.java` / `EmailServiceImpl.java`

### `controller/`
- [ ] `AuthController.java` — register, login, forgot-password, reset-password, verify-email, refresh-token

### `exception/`
- [ ] `GlobalExceptionHandler.java`
- [ ] `ApiError.java`
- [ ] `ResourceNotFoundException.java`, `BadRequestException.java`, `UnauthorizedException.java`

### `util/`
- [ ] `JwtUtil.java`, `AppConstants.java`

**Deliverable at end of Phase 1:** runnable Spring Boot app with working register/login/JWT/roles, tested via curl/Postman collection.

---

## PHASE 2 — Student Profile Module

- [ ] Entities: `Student`, `Education`, `Skill`, `StudentSkill`, `Project`, `Certification`, `Achievement`
- [ ] DTOs + Mappers for each (request/response)
- [ ] Repositories for each
- [ ] `StudentService` / `StudentServiceImpl`
- [ ] `StudentController` — CRUD for profile, education, skills, projects, certifications, achievements
- [ ] Validation annotations + custom validators
- [ ] Postman collection update

---

## PHASE 3 — Resume Module (Upload, Parse, AI Analyze)

- [ ] `FileStorageConfig.java` + `FileStorageService.java` (local storage)
- [ ] Entities: `Resume`, `ResumeAnalysis`
- [ ] `ResumeController` — upload, download, list, delete
- [ ] `ResumeParserService` (extract text from PDF/DOCX)
- [ ] `GeminiClient.java` (Gemini API wrapper — resume scoring, suggestions, skill extraction, career prediction)
- [ ] `ResumeAnalysisService` — orchestrates parse + AI call + persist
- [ ] DTOs: `ResumeUploadResponse`, `ResumeAnalysisResponse`

---

## PHASE 4 — Digital Twin Module

- [ ] Entity: `DigitalTwin`
- [ ] `DigitalTwinService` — builds twin from profile + resume + behavior data via Gemini
- [ ] `DigitalTwinController`
- [ ] AI prompt templates for: behavior prediction, learning pattern analysis

---

## PHASE 5 — Interview Module

- [ ] Entities: `QuestionBank`, `InterviewSession`, `InterviewQuestion`, `InterviewAnswer`, `InterviewFeedback`
- [ ] `InterviewGeneratorService` (Gemini-generated questions by role/skill)
- [ ] `MockInterviewService` (session lifecycle: start, submit answer, end)
- [ ] `InterviewEvaluationService` (Gemini feedback: confidence, strengths, weaknesses, score)
- [ ] `InterviewController`, `QuestionBankController`

---

## PHASE 6 — Skill Gap, Roadmap, Analytics, Notifications

- [ ] Entities: `SkillGapAnalysis`, `Roadmap`, `RoadmapItem`, `Notification`
- [ ] `SkillGapService` (compares student skills vs target role via Gemini)
- [ ] `RoadmapService` (course/project/certification recommendations)
- [ ] `AnalyticsService` (dashboard aggregates, charts data)
- [ ] `NotificationService`
- [ ] Corresponding controllers

---

## PHASE 7 — Admin Module

- [ ] `AdminDashboardController` — stats aggregation
- [ ] `AdminStudentController` — manage students
- [ ] `AdminInterviewController` — manage interviews/questions
- [ ] `AdminSkillController` — manage skills master
- [ ] `AdminPromptController` — manage AI prompt templates (`AiPrompt` entity)
- [ ] `AdminReportController` — generate/download reports (PDF)
- [ ] `AdminSettingsController` — system settings

---

## PHASE 8 — Frontend Foundation

### Setup
- [ ] `vite.config.js`, `tailwind.config.js`, `package.json`
- [ ] `src/main.jsx`, `src/App.jsx`, `src/router.jsx`

### `src/context/`
- [ ] `AuthContext.jsx`, `ThemeContext.jsx` (dark mode), `NotificationContext.jsx`

### `src/services/` (Axios)
- [ ] `apiClient.js`, `authService.js`, `studentService.js`, `resumeService.js`, `interviewService.js`, `adminService.js`

### `src/hooks/`
- [ ] `useAuth.js`, `useTheme.js`, `useFetch.js`

### `src/layouts/`
- [ ] `MainLayout.jsx`, `AuthLayout.jsx`, `DashboardLayout.jsx` (animated sidebar, topbar)

### `src/components/common/`
- [ ] `Button`, `Card`, `Modal`, `Loader`, `Toast`, `ProtectedRoute`, `GlassCard`, `Badge`, `EmptyState`

### `src/pages/auth/`
- [ ] `Login.jsx`, `Register.jsx`, `ForgotPassword.jsx`, `ResetPassword.jsx`, `VerifyEmail.jsx`

**Deliverable at end of Phase 8:** running frontend shell with auth flow wired to backend from Phase 1, dark mode, protected routing.

---

## PHASE 9 — Student Frontend

- [ ] `pages/student/Dashboard.jsx` (Recharts widgets)
- [ ] `pages/student/Profile.jsx`, `Education.jsx`, `Skills.jsx`, `Projects.jsx`, `Certifications.jsx`, `Achievements.jsx`
- [ ] `pages/student/ResumeUpload.jsx`, `ResumeAnalysis.jsx`
- [ ] `pages/student/DigitalTwin.jsx`
- [ ] `pages/student/InterviewPrep.jsx`, `MockInterview.jsx`, `InterviewResult.jsx`
- [ ] `pages/student/SkillGap.jsx`, `Roadmap.jsx`
- [ ] `pages/student/Analytics.jsx`
- [ ] `pages/student/Notifications.jsx`, `Settings.jsx`
- [ ] Corresponding forms (React Hook Form + validation schemas)

---

## PHASE 10 — Admin Frontend

- [ ] `pages/admin/Dashboard.jsx`
- [ ] `pages/admin/Students.jsx`, `StudentDetail.jsx`
- [ ] `pages/admin/Interviews.jsx`, `QuestionBank.jsx`
- [ ] `pages/admin/Skills.jsx`
- [ ] `pages/admin/Prompts.jsx`
- [ ] `pages/admin/Reports.jsx`
- [ ] `pages/admin/Analytics.jsx`
- [ ] `pages/admin/Settings.jsx`

---

## PHASE 11 — Deployment & DevOps

- [ ] `docker/backend.Dockerfile`
- [ ] `docker/frontend.Dockerfile`
- [ ] `docker/nginx.conf`
- [ ] `docker-compose.yml` (mysql, backend, frontend, nginx)
- [ ] `.env.example` (backend + frontend)
- [ ] `.github/workflows/ci.yml` (build + test)
- [ ] `.github/workflows/cd.yml` (build + push images)
- [ ] `README.md`, `INSTALLATION.md`, `DEPLOYMENT.md`, `TESTING.md`

---

## How we'll work through this

Each phase = one working, testable increment. I'll generate the real code for a phase, you review/run it, then we move to the next phase so context stays clean and nothing breaks silently downstream.

**Suggested starting point:** Phase 0 (schema + repo structure) → Phase 1 (Auth). Say the word and I'll start generating actual code for Phase 0.
