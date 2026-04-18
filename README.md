# LogShield

**Detect and redact sensitive data in log files — instantly.**

LogShield is a full-stack portfolio project that accepts `.log` and `.txt` uploads, scans them for credentials and PII, returns a redacted copy, and persists a searchable scan history.

[![Frontend CI](https://github.com/awnonbhowmik/LogShield/actions/workflows/frontend.yml/badge.svg)](https://github.com/awnonbhowmik/LogShield/actions/workflows/frontend.yml)
[![Backend CI](https://github.com/awnonbhowmik/LogShield/actions/workflows/backend.yml/badge.svg)](https://github.com/awnonbhowmik/LogShield/actions/workflows/backend.yml)

---

## Features

| Feature | Detail |
|---------|--------|
| **Detection** | Emails · IPv4 addresses · JWT tokens · API keys · Credit card numbers |
| **Redaction** | Placeholder substitution per category (`[REDACTED_EMAIL]`, etc.) |
| **Severity scoring** | Category-weighted 0–100 score, bands: LOW / MEDIUM / HIGH / CRITICAL |
| **Scan history** | Persisted in PostgreSQL, searchable by filename, severity, and date |
| **Redacted download** | One-click download of the sanitised file |
| **Copy to clipboard** | Copy redacted output directly from the preview |
| **Dark UI** | Next.js 16 · TailwindCSS v4 · fully responsive |
| **Docker** | Single `docker compose up` starts the entire stack |

---

## Architecture

```
┌─────────────────┐       HTTP / JSON        ┌─────────────────────┐
│  Next.js 16     │ ──────────────────────── │  Spring Boot 3      │
│  (port 3000)    │   POST /api/scans         │  (port 8080)        │
│                 │   GET  /api/scans         │                     │
│  App Router     │   GET  /api/scans/:id     │  Detection Engine   │
│  TailwindCSS v4 │   GET  /api/scans/:id/    │  Redaction Service  │
│  TypeScript     │        download           │  Severity Scorer    │
└─────────────────┘                           └──────────┬──────────┘
                                                         │  JPA / Hibernate
                                              ┌──────────┴──────────┐
                                              │   PostgreSQL 16      │
                                              │   scan_job           │
                                              │   scan_finding       │
                                              └─────────────────────┘
```

### Backend package layout

```
com.logshield.backend/
├── controller/       REST endpoints (ScanController)
├── service/          Business logic (ScanService + impl)
├── scanner/          Detection engine, rules, redactor, scorer
│   └── rules/        EmailRule, IpAddressRule, JwtTokenRule, ApiKeyRule, CreditCardRule
├── entity/           JPA entities (ScanJob, ScanFinding)
├── dto/              Request/response records
├── repository/       Spring Data interfaces
├── validation/       FileValidator
└── exception/        Custom exceptions + GlobalExceptionHandler
```

### Frontend page map

| Route | Rendering | Purpose |
|-------|-----------|---------|
| `/` | Server | Landing page |
| `/upload` | Server shell + Client card | File upload + inline results |
| `/history` | Server async | Scan list with live filters |
| `/scans/[id]` | Server async | Full scan detail + download |

---

## Screenshots

> _Capture these after your first run and drop the images into `docs/screenshots/`._

| Screen | Description |
|--------|-------------|
| `01-landing.png` | Hero page with feature pills |
| `02-upload.png` | Drop zone before file is selected |
| `03-scanning.png` | Spinner during scan |
| `04-results.png` | Summary cards + findings table + redacted preview |
| `05-history.png` | History table with filters active |
| `06-detail.png` | Scan detail page with download button |

---

## Local setup (without Docker)

### Prerequisites

| Tool | Version |
|------|---------|
| Node.js | 20+ |
| Java | 25 |
| PostgreSQL | 15+ |

### 1 — Database

```sql
CREATE DATABASE logshield;
```

### 2 — Backend

```bash
cd backend
./mvnw spring-boot:run
# API → http://localhost:8080
```

Edit `src/main/resources/application.properties` if your PostgreSQL credentials differ from `postgres/postgres`.

### 3 — Frontend

```bash
cd frontend
cp .env.local.example .env.local   # no changes needed for local dev
npm install
npm run dev
# UI  → http://localhost:3000
```

---

## Docker setup (recommended)

> **One command starts everything** — PostgreSQL, Spring Boot, and Next.js.

```bash
docker compose up --build
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| PostgreSQL | localhost:5432 |

To stop and remove volumes:

```bash
docker compose down -v
```

---

## Environment variables

### Frontend (`frontend/.env.local`)

| Variable | Default | Purpose |
|----------|---------|---------|
| `API_URL` | `http://localhost:8080` | Rewrite proxy target (server startup) |
| `INTERNAL_API_URL` | `http://localhost:8080` | Server-side fetch base URL |

### Backend (`application.properties`)

| Property | Default | Purpose |
|----------|---------|---------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/logshield` | DB connection |
| `spring.datasource.username` | `postgres` | DB user |
| `spring.datasource.password` | `postgres` | DB password |
| `spring.servlet.multipart.max-file-size` | `10MB` | Upload limit |

---

## API overview

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/scans` | Upload a `.log`/`.txt` file, scan and persist |
| `GET`  | `/api/scans` | List all scan jobs (newest first) |
| `GET`  | `/api/scans/{id}` | Full detail for one scan job |
| `GET`  | `/api/scans/{id}/download` | Download redacted file as attachment |

### POST /api/scans — example response

```json
{
  "id": 1,
  "filename": "server.log",
  "uploadedAt": "2024-01-15T10:30:00",
  "status": "COMPLETED",
  "severityScore": 75,
  "totalFindings": 8,
  "findingsByType": {
    "EMAIL":       [{ "id": 1, "matchedValue": "user@corp.com", "redactedValue": "[REDACTED_EMAIL]", "lineNumber": 1, "severity": "LOW" }],
    "JWT_TOKEN":   [{ "id": 3, "matchedValue": "eyJ...", "redactedValue": "[REDACTED_JWT]", "lineNumber": 2, "severity": "HIGH" }],
    "CREDIT_CARD": [{ "id": 4, "matchedValue": "4111111111111111", "redactedValue": "[REDACTED_CARD]", "lineNumber": 3, "severity": "CRITICAL" }]
  },
  "redactedPreview": "2024-01-15 10:00:01 INFO user=[REDACTED_EMAIL] ip=[REDACTED_IP]..."
}
```

---

## Sample files

Two ready-to-upload samples live in `samples/`:

| File | Contents |
|------|----------|
| `samples/clean.log` | Standard application log — no sensitive data |
| `samples/sensitive.log` | Log with email, IP, JWT, API key, and credit card data |

---

## Severity scoring

| Score | Band | Typical trigger |
|-------|------|-----------------|
| 0 | — | No findings |
| 1–15 | **LOW** | Emails or IPs only (5 pts each) |
| 16–40 | **MEDIUM** | One JWT (20 pts) or one API key (25 pts) |
| 41–75 | **HIGH** | API key + email, or multiple JWTs |
| 76–100 | **CRITICAL** | Multiple credentials — capped at 100 |

---

## Roadmap

- [ ] **OAuth / JWT auth** — protect the scan API with user accounts
- [ ] **Luhn validation** — confirm credit card matches pass the Luhn checksum before flagging
- [ ] **Custom rules** — let users upload regex rules via the UI
- [ ] **Bulk upload** — scan a ZIP archive containing multiple log files
- [ ] **Export report** — generate a PDF/CSV summary of findings
- [ ] **Testcontainers** — full integration tests against a real PostgreSQL container
- [ ] **Rate limiting** — throttle uploads per IP with Spring's filter chain
- [ ] **S3 storage** — store original and redacted files in object storage instead of DB columns

---

## Tech stack

| Layer | Technology |
|-------|-----------|
| Frontend | Next.js 16, React 19, TailwindCSS v4, TypeScript |
| Backend | Java 25, Spring Boot 3.2, Spring Data JPA, Lombok |
| Database | PostgreSQL 16 |
| Build | Maven Wrapper, npm |
| CI | GitHub Actions |
| Infra | Docker, Docker Compose |
