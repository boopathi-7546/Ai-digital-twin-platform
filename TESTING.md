# Testing Guide

## Backend — automated tests

```bash
cd backend
mvn clean test
```

Add unit tests under `src/test/java/com/digitaltwin/platform/...` mirroring the main package structure. Recommended starting points, if extending this scaffold:

- `AuthServiceImplTest` — register/login/verify/reset flows, using Mockito for `UserRepository`/`EmailService`.
- `StudentServiceImplTest` — ownership boundary checks (a student can't touch another student's sub-resources).
- `GeminiClientTest` — mock the HTTP layer (`RestClient`) and assert `stripJsonFences` handles fenced/unfenced responses.
- Controller slice tests with `@WebMvcTest` + `@WithMockUser` for role-based access checks (e.g. a STUDENT-authenticated request to `/api/admin/**` returns 403).

## Backend — manual API testing (Swagger)

1. Start the backend (`mvn spring-boot:run`, `dev` profile).
2. Open **http://localhost:8080/swagger-ui.html**.
3. `POST /api/auth/register`, then verify the account (see INSTALLATION.md if SMTP isn't configured locally).
4. `POST /api/auth/login` → copy `accessToken`.
5. Click **Authorize** in Swagger UI, paste `Bearer <accessToken>`.
6. Exercise endpoints in this order for a realistic end-to-end flow:
   - `PUT /api/student/profile` → fill in target role, degree, etc.
   - `POST /api/student/skills` → add a few skills (get valid `skillId`s from `GET /api/admin/skills`, which requires an admin-authenticated request — log in as `admin@digitaltwin.io` in a second Swagger session, or check `database/seed_data.sql` for the seeded skill names and query their IDs directly in MySQL: `SELECT id, name FROM skills;`)
   - `POST /api/student/resumes/upload` (multipart) → `POST /api/student/resumes/{id}/analyze`
   - `POST /api/student/digital-twin/regenerate`
   - `POST /api/student/interviews/start` → `POST .../answers` for each question → `POST .../complete`
   - `POST /api/student/skill-gap/analyze` → `POST /api/student/roadmaps/generate/{skillGapAnalysisId}`
   - `GET /api/student/analytics` to confirm the dashboard aggregates reflect the above

## Frontend — automated tests

If you add a test runner (Vitest recommended for a Vite project):

```bash
cd frontend
npm install -D vitest @testing-library/react @testing-library/jest-dom jsdom
npm run test
```

Suggested coverage:

- `AuthContext` — login persists tokens to localStorage; logout clears them.
- `ProtectedRoute` — redirects unauthenticated users to `/login`; redirects wrong-role users away from admin/student-only subtrees.
- Form validation on `Register`/`Login`/`ResetPassword` (password pattern, confirm-password match).

## Frontend — manual testing checklist

1. `npm run dev`, open http://localhost:5173.
2. **Auth flow**: register → (verify email) → login → confirm redirect to `/student/dashboard`.
3. **Profile**: add education, a skill, a project, a certification, an achievement; confirm each tab reflects saved data after refresh.
4. **Resume**: upload a PDF, click Analyze, confirm score/skills/strengths/weaknesses render.
5. **Digital twin**: click Generate, confirm behavior/learning/career sections populate.
6. **Mock interview**: start a session, answer every question, complete it, confirm feedback scores render.
7. **Skill gap → Roadmap**: run an analysis, generate a roadmap from it, toggle an item complete, confirm the progress bar updates.
8. **Analytics**: confirm charts render (or show empty states gracefully with no data).
9. **Notifications**: confirm the bell's unread count matches the notifications page.
10. **Admin**: log in as `admin@digitaltwin.io`, confirm dashboard stats, student list (toggle a student's active status), skills CRUD, question bank CRUD, prompt template edit, report generation + download.
11. **Dark/light theme toggle** in the topbar and Settings page.
12. **Responsive check**: resize to a mobile width, confirm the sidebar collapses behind the hamburger menu.

## Load / integration testing (optional)

For a more rigorous pre-production pass, point a tool like k6 or JMeter at the Docker Compose stack (`docker compose up --build`) hitting `/api/auth/login` and a couple of read-heavy endpoints (`/api/student/analytics`, `/api/admin/dashboard`) to validate connection pool sizing (`spring.datasource.hikari.*` in `application.yml`) under concurrent load.
