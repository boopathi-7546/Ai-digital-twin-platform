# Installation Guide

This guide covers running the platform **natively** (backend via Maven, frontend via Vite, MySQL installed locally or in a standalone container) — useful for active development. For a one-command full-stack setup, use Docker Compose instead (see `DEPLOYMENT.md`).

## Prerequisites

- **Java 21** (Temurin recommended)
- **Maven 3.9+**
- **Node.js 20+** and npm
- **MySQL 8.x** (local install, or `docker run -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=digital_twin_platform mysql:8.4`)
- A **Gemini API key** (https://ai.google.dev/) — required for every AI feature
- An SMTP account (e.g. a Gmail app password) for sending verification/reset emails — optional in dev, but registration/login will feel broken without it since verification emails won't arrive

## 1. Database

If MySQL is running locally with an empty `digital_twin_platform` schema, you don't need to create tables manually — **Flyway runs `database/migrations/V1__init.sql` automatically the first time the backend starts** (see `backend/src/main/resources/db/migration/V1__init.sql`, mirrored from the same file). Just make sure an empty schema exists:

```sql
CREATE DATABASE IF NOT EXISTS digital_twin_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Do **not** run `database/seed_data.sql` yet — the tables it inserts into don't exist until after step 2 (starting the backend) has run once. Seeding happens in step 3.

## 2. Backend

```bash
cd backend
cp .env.example .env
# Edit .env: set JWT_SECRET, GEMINI_API_KEY, MAIL_USERNAME, MAIL_PASSWORD, DB_* as needed

# Export the .env file into your shell (or use an IDE run configuration / direnv)
export $(grep -v '^#' .env | xargs)

mvn spring-boot:run
```

The backend starts on **http://localhost:8080**. Verify it's up:

```bash
curl http://localhost:8080/actuator/health
```

Swagger UI (interactive API docs): **http://localhost:8080/swagger-ui.html**

### Common backend issues

| Symptom | Fix |
|---|---|
| `Access denied for user` on startup | Check `DB_USERNAME`/`DB_PASSWORD` match your MySQL user |
| Flyway checksum mismatch | Don't hand-edit `V1__init.sql` after it's been applied; add a new `V2__...sql` instead |
| 500 errors from any AI endpoint | `GEMINI_API_KEY` is missing/invalid — check `.env` |
| Registration succeeds but no email arrives | `MAIL_USERNAME`/`MAIL_PASSWORD` not set, or your SMTP provider needs an app-specific password |

## 3. Seed sample data (optional, recommended for development)

Now that the backend has started at least once (so Flyway has created the schema), you can safely load sample roles/users/skills/questions:

```bash
mysql -u root -p digital_twin_platform < database/seed_data.sql
```

If you already registered your own account in step 2, this is still safe to run — it only inserts new rows and won't touch your account.

## 4. Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

The frontend starts on **http://localhost:5173** and proxies `/api` and `/uploads` requests to `http://localhost:8080` (see `vite.config.js`) — no CORS configuration needed in dev beyond what's already in `application-dev.yml`.

## 5. First login

Use one of the seeded accounts (see `README.md`) with password `Password@123`, or register a new student account — you'll need working SMTP configured to receive the verification email, or you can manually mark a user verified in MySQL for local testing:

```sql
UPDATE users SET is_email_verified = TRUE WHERE email = 'you@example.com';
```

## 6. Running both together day-to-day

Two terminals:

```bash
# Terminal 1
cd backend && mvn spring-boot:run

# Terminal 2
cd frontend && npm run dev
```

Then open **http://localhost:5173**.
