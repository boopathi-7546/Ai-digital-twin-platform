# Deployment Guide

## Option A — Docker Compose (recommended for staging / small production)

This spins up MySQL, the Spring Boot backend, and the React frontend (served by its own Nginx, which also reverse-proxies API/upload requests to the backend) in one command.

```bash
cp backend/.env.example .env
# Edit .env at the repo root with production values — at minimum:
#   JWT_SECRET (long random value, e.g. `openssl rand -base64 48`)
#   GEMINI_API_KEY
#   MAIL_USERNAME / MAIL_PASSWORD
#   DB_ROOT_PASSWORD / DB_PASSWORD (don't use the example defaults)
#   CORS_ALLOWED_ORIGINS / FRONTEND_BASE_URL (your real domain)

docker compose up --build -d
```

- Frontend (and reverse-proxied API): `http://<your-server>`
- Direct backend access (optional, e.g. for Swagger): `http://<your-server>:8080`

Check health:

```bash
docker compose ps
curl http://localhost:8080/actuator/health   # backend, exposed directly on :8080
curl http://localhost/                        # frontend, served on :80 via Nginx
```

Note: `docker/nginx.conf` only proxies `/api/`, `/uploads/`, `/swagger-ui/`, and `/api-docs` through port 80 — `/actuator/health` is only reachable on the backend's own port (8080), not through the frontend Nginx. If you want health checks through a single public port, add an `/actuator/` location block to `docker/nginx.conf`.

### Updating

```bash
git pull
docker compose up --build -d
```

Flyway applies any new `V2__*.sql` migrations automatically on backend startup — never edit an already-applied migration file.

### Backups

The MySQL data directory is a named volume (`mysql_data`). Back it up like any other MySQL instance:

```bash
docker exec dtp_mysql mysqldump -u root -p"$DB_ROOT_PASSWORD" digital_twin_platform > backup.sql
```

Uploaded resumes live in the `backend_uploads` volume — back this up too, or point `UPLOAD_DIR`/the volume mount at network storage for durability.

## Option B — Manual / VM deployment

1. Build the backend jar: `cd backend && mvn clean package -DskipTests` → `target/digital-twin-platform.jar`
2. Run it behind a process manager (systemd, supervisor) with `SPRING_PROFILES_ACTIVE=prod` and all required env vars set.
3. Build the frontend: `cd frontend && npm run build` → `dist/`
4. Serve `dist/` with any static file server (Nginx, Caddy, S3+CloudFront) and reverse-proxy `/api` and `/uploads` to the backend — see `docker/nginx.conf` for a working example to adapt.
5. Point MySQL at a managed instance (RDS, Cloud SQL, etc.) instead of a local container for production durability.

## Option C — Kubernetes

The Docker images built by `docker/backend.Dockerfile` and `docker/frontend.Dockerfile` (and pushed by `.github/workflows/cd.yml` to GHCR) are plain, stateless containers and can be deployed with standard Deployment + Service + Ingress manifests:

- Backend: mount a PVC for `UPLOAD_DIR` (or switch to S3/GCS-backed storage — `FileStorageService` is an interface specifically so this swap doesn't touch callers), inject secrets via a Kubernetes Secret, point `DB_URL` at your MySQL/Cloud SQL instance.
- Frontend: stateless, scale horizontally freely; the Nginx config's `/api` proxy target (`backend:8080`) should become a Kubernetes Service DNS name — override via a ConfigMap-mounted `nginx.conf` per environment.

## Production checklist

- [ ] `JWT_SECRET` is a strong, unique random value (never the example placeholder)
- [ ] `SPRING_PROFILES_ACTIVE=prod` (disables Swagger UI, verbose SQL logging, and stack traces in error responses — see `application-prod.yml`)
- [ ] `CORS_ALLOWED_ORIGINS` is your real frontend origin(s) only
- [ ] MySQL credentials are not the `.env.example` defaults
- [ ] `GEMINI_API_KEY` is a production key (or leave AI features degraded with clear errors if you're rolling out access gradually)
- [ ] File uploads are backed by durable/replicated storage, not a single container's ephemeral disk
- [ ] TLS terminates somewhere in front of Nginx (a load balancer, or add a `443` server block + certs to `docker/nginx.conf`)
- [ ] Database backups are scheduled
