# MilestoneMetrics — Backend

Spring Boot 3 + Spring Security 6 + PostgreSQL (Supabase) + JWT authentication API.

## Tech stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Language    | Java 17 (source/target)           |
| Runtime     | Java 26+ (any version ≥ 17 works) |
| Framework   | Spring Boot 3.2.5                 |
| Security    | Spring Security 6 + JWT (jjwt)   |
| Database    | PostgreSQL via Supabase           |
| Build       | Maven (via `mvnw.cmd` wrapper)    |

## Prerequisites

- Java installed and on your PATH (or `JAVA_HOME` set)
- A Supabase project with a PostgreSQL database
- A `.env` file at `code/backend/.env` (see below)

## Environment variables

Create `code/backend/.env` (never commit this file):

```
DB_URL=jdbc:postgresql://<your-supabase-host>:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=<your-supabase-db-password>
JWT_SECRET=<random-string-at-least-32-chars>
```

## Running locally (Windows)

### Step 1 — Load env vars into PowerShell

Open PowerShell in the repo root (`D:\MilestoneMetrics`), then run:

```powershell
Get-Content "code\backend\.env" | ForEach-Object {
    if ($_ -match '^([^#=][^=]*)=(.+)$') {
        [System.Environment]::SetEnvironmentVariable($Matches[1].Trim(), $Matches[2].Trim(), 'Process')
    }
}
```

### Step 2 — Start the backend

```powershell
cd code\backend
.\mvnw.cmd spring-boot:run
```

Backend starts on **http://localhost:8080**. You will see:
```
HikariPool-1 - Start completed.
Started BackendApplication in X seconds
```

### Step 3 — Start the frontend (separate terminal)

```powershell
cd code\frontend
npm install
npm run dev
```

Frontend starts on **http://localhost:5173**.

## API endpoints

All endpoints are under `/api/auth/`. No authentication header needed.

### POST /api/auth/signup

```bash
curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane","email":"jane@example.com","password":"secret123"}'
```

**201 Created:**
```json
{"message": "User registered successfully"}
```

**409 Conflict** (email already exists):
```json
{"status": 409, "message": "An account with this email already exists"}
```

### POST /api/auth/login

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"secret123"}'
```

**200 OK:**
```json
{"token": "<jwt>", "email": "jane@example.com", "name": "Jane"}
```

**401 Unauthorized** (wrong credentials):
```json
{"status": 401, "message": "Invalid email or password"}
```

## PowerShell test commands

```powershell
# Signup
$r = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/signup" `
  -Method POST -ContentType "application/json" `
  -Body '{"name":"Test","email":"test@example.com","password":"pass123"}' `
  -ErrorAction SilentlyContinue
"Status: $($r.StatusCode)  Body: $($r.Content)"

# Login
$r = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
  -Method POST -ContentType "application/json" `
  -Body '{"email":"test@example.com","password":"pass123"}' `
  -ErrorAction SilentlyContinue
"Status: $($r.StatusCode)  Body: $($r.Content)"
```

## JWT

- Algorithm: HS256
- Expiry: 24 hours (86400000 ms)
- Header name: `Authorization: Bearer <token>`
- Decode at [jwt.io](https://jwt.io) for inspection

## Database

- Table: `users` (auto-created by Hibernate on first run)
- Password column: BCrypt hash (`$2a$10$...`)
- Verify in Supabase Dashboard → Table Editor → `users`

## Troubleshooting

**Port 8080 already in use**
```powershell
netstat -ano | findstr :8080
Stop-Process -Id <PID> -Force
```

**Cannot connect to database**
- Check that env vars loaded (see Step 1)
- Verify DB_URL uses `sslmode=require`
- Supabase requires the password of the `postgres` user (Database Settings page)

**Frontend shows "Could not reach the server"**
- Ensure backend is running on port 8080
- CORS is configured for `http://localhost:5173` only
