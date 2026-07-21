# AI-Powered Digital Twin & Interview Readiness Platform

An enterprise-style web application where students build a profile, upload a resume for AI analysis, generate a "digital twin" of their skills/behavior, practice AI mock interviews, track skill gaps, and follow a personalized learning roadmap. Admins manage students, the curated question bank, the skills catalog, AI prompt templates, reports, and platform analytics.

## Tech stack

| Layer      | Technology |
|------------|------------|
| Frontend   | React 19, Vite, React Router, Axios, Tailwind CSS, Framer Motion, Recharts, React Hook Form |
| Backend    | Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA/Hibernate, Flyway, Maven |
| Database   | MySQL 8 |
| AI         | Google Gemini API |
| Storage    | Local filesystem (resumes) |
| Deployment | Docker, Docker Compose, Nginx, GitHub Actions |

## Repository structure

```
digital-twin-platform/
├── backend/                 Spring Boot 3 REST API
├── frontend/                React 19 + Vite SPA (frontend/public/ is Vite's static-asset root)
├── database/                Raw SQL schema, seed data, and the Flyway migration
├── docker/                  Dockerfiles + Nginx config
├── docker-compose.yml        Full local/staging stack (MySQL + backend + frontend)
├── .github/workflows/        CI (build/test) and CD (build/push images) pipelines
├── .gitignore                 Ignores build output, node_modules, .env files, uploads
├── BUILD_PLAN.md               Phase-by-phase build plan this repo was generated from
├── INSTALLATION.md             Local setup, step by step
├── DEPLOYMENT.md                Docker Compose / production deployment guide
└── TESTING.md                   How to exercise the API and UI manually and via automated tests
```

## Quick start (Docker Compose)

```bash
git clone <this-repo>
cd digital-twin-platform
cp backend/.env.example .env      # fill in JWT_SECRET, GEMINI_API_KEY, MAIL_* at minimum
docker compose up --build
```

- Frontend: http://localhost
- Backend Swagger UI: http://localhost:8080/swagger-ui.html
- MySQL: localhost:3306 (see `.env` for credentials)

See **INSTALLATION.md** for running the backend and frontend natively (without Docker) during development, and **DEPLOYMENT.md** for production notes.

## Seeded accounts

Flyway automatically creates the schema (`database/migrations/V1__init.sql`) the first time the backend starts. Sample data (`database/seed_data.sql`) is a separate step — it's applied automatically by Docker Compose (via MySQL's init scripts) but must be run manually for a native/Maven setup (see **INSTALLATION.md**, step 3). Once seeded, these accounts exist with password `Password@123`:

| Role    | Email                       |
|---------|------------------------------|
| Admin   | admin@digitaltwin.io          |
| Student | ananya.rao@example.com        |
| Student | rahul.mehta@example.com       |
| Student | sneha.iyer@example.com        |

## Key features by module

- **Auth**: register, email verification, login (JWT access + refresh tokens), forgot/reset password.
- **Student profile**: education, skills, projects, certifications, achievements.
- **Resume**: upload (PDF/DOCX), AI scoring, skill extraction, strengths/weaknesses/suggestions, predicted roles.
- **Digital twin**: AI-derived behavior profile, learning pattern, and career prediction, regenerated on demand.
- **Mock interview**: AI-generated questions per target role/difficulty, answer submission, AI feedback (confidence/communication/technical scores).
- **Skill gap & roadmap**: AI comparison of current skills vs. a target role, then an AI-generated roadmap of courses/projects/certifications/skills with completion tracking.
- **Analytics & notifications**: dashboards for students and admins, in-app notifications.
- **Admin console**: manage students (activate/deactivate), the question bank, the skills catalog, AI prompt templates (editable without redeploying), and generate/download reports.

## Environment variables

See `backend/.env.example` and `frontend/.env.example` for the full list. At minimum, set:

- `JWT_SECRET` — a long random string (never use the example value in production)
- `GEMINI_API_KEY` — required for every AI feature (resume analysis, digital twin, interviews, skill gap, roadmap)
- `MAIL_USERNAME` / `MAIL_PASSWORD` — required for verification/reset emails to actually send

## License

Internal/enterprise project scaffold — adapt licensing as appropriate for your organization.
