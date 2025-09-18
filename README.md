
# Oli CMS — Multi-tenant CMS (Spring Boot + Next.js)

![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5+-green.svg)
![Next.js](https://img.shields.io/badge/Next.js-14+-blue.svg)
![Java](https://img.shields.io/badge/Java-21+-blue.svg)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)
![Docker](https://img.shields.io/badge/Docker-latest-blue.svg)
[![CodeQL](https://github.com/kanghouchao/MYCMS/actions/workflows/codeql.yml/badge.svg)](https://github.com/kanghouchao/MYCMS/actions/workflows/codeql.yml)
[![Dependabot](https://img.shields.io/badge/Dependabot-enabled-brightgreen.svg)](https://github.com/kanghouchao/MYCMS/security/dependabot)

Oli CMS is a modern, multi-tenant Content Management System built with a split architecture: Spring Boot backend and Next.js frontend, orchestrated with Docker and Traefik.

## Highlights

- Multi-tenant by host name: one frontend, isolated tenant contexts
- Split architecture: Spring Boot API + Next.js app
- Stateless JWT auth; admin (central) and tenant APIs split
- Responsive UI with Tailwind CSS
- Container-first: easy local dev and ops via Make + Docker

## Tech Stack

| Area | Tech | Version |
|------|------|---------|
| Backend | Spring Boot | 3.5+ |
| Frontend | Next.js | 14+ |
| Language | Java | 21+ |
| DB | PostgreSQL | 16+ |
| Cache | Redis | 7+ |
| Proxy | Traefik | 3.5+ |
| Containers | Docker | latest |
| Styling | Tailwind CSS | 3.4+ |
| Auth | JWT (stateless) | - |

## Architecture

Traefik routes all requests to the right service. The frontend and backend are fully decoupled and communicate over HTTP. All frontend API calls go through the reverse proxy under the `/api` prefix.

```text
                   ┌─────────────────┐
                   │   Frontend      │
   ┌──────────────►│   Next.js (3000)│
   │               └─────────────────┘
┌───────────┐
│ Traefik   │           ┌─────────────────┐
│ :80       │◄──────────►│  Backend        │
└───────────┘            │  Spring Boot    │
                         │  :8080          │
                         └─────────────────┘
                                   │
                          ┌─────────────────┐
                          │ PostgreSQL/Redis│
                          └─────────────────┘
```

- Routing: `/api/*` → backend (prefix stripped before reaching Spring); everything else → frontend
- Backend API namespaces: `/central/*` (admin) and `/tenant/*` (tenant)
- Domain switch (frontend middleware): admin domains render the central app, others render the tenant app

### Multi-tenant flow and cookies

- Frontend middleware decides the role based on the host name and validates the tenant via backend
- Middleware sets cookies for server components to read:
  - `x-mw-role`: `central | tenant`
  - `x-mw-tenant-template`: template key to load SSR tenant page
  - `x-mw-tenant-id`, `x-mw-tenant-name`: tenant meta
- In server components, read via `cookies()` (not raw `headers()`).

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Make

### Setup

1. Clone the repo

```bash
git clone https://github.com/kanghouchao/MYCMS.git
cd MYCMS
```

1. Start services

```bash
make build up
```

1. Map local domains (for admin/tenant switching)

Add the following lines to `/etc/hosts`:

```text
127.0.0.1 oli-cms.test
127.0.0.1 tenant.example.test  # sample tenant domain
```

1. Access

- Central (admin UI): [oli-cms.test](http://oli-cms.test)
- Sample tenant: [tenant.example.test](http://tenant.example.test)

1. First-time admin setup

- Use the central registration screen to create the first admin account: `/central/register` on the admin domain
- No default password is published in this repo for security reasons

### Useful Make targets

- `make help` — list all commands
- `make build` — build backend and frontend images
- `make up` — start the full stack (Traefik, DB, Redis, backend, frontend)
- `make down` — stop and remove containers
- `make ps` — show running services
- `make logs service=backend|frontend|traefik` — follow service logs
- `make test` or `make test service=backend|frontend` — run tests

## Development Notes

- All frontend API traffic must go through `/api` so Traefik can route and strip the prefix
- Central domain configuration lives in two places:
  - `frontend/src/middleware.ts` → `ADMIN_DOMAINS`
  - `NEXT_PUBLIC_CENTRAL_DOMAIN` env (used by `AuthContext`)
- Backend health endpoint: `/actuator/health`
- Tenant templates live under `frontend/src/app/tenant/templates/<key>/page.tsx`
  - To add a template: create the folder, export a `page.tsx`, and ensure the tenant validation API returns `template_key` matching the folder name

## Project Structure

```text
MYCMS/
├── backend/                     # Spring Boot API
│   ├── src/
│   │   ├── main/java/com/cms/
│   │   ├── main/resources/
│   │   └── test/
│   ├── build.gradle
│   └── Dockerfile
├── frontend/                    # Next.js app
│   ├── src/
│   │   ├── middleware.ts
│   │   ├── app/
│   │   │   ├── central/
│   │   │   ├── tenant/
│   │   │   └── login/
│   │   ├── contexts/
│   │   ├── lib/
│   │   └── services/
│   └── package.json
├── traefik/
│   ├── development/
│   └── production/
├── docker-compose.yml
└── Makefile
```

## Troubleshooting

- If ports are busy, ensure nothing else is using 80 (Traefik), 3000 (frontend), or 8080 (backend)
- Confirm `/etc/hosts` entries resolve to your machine
- If the frontend shows a 401 and redirects to `/login`, your token may be missing or expired; log in again

## Support

- Open an issue: [github.com/kanghouchao/MYCMS/issues](https://github.com/kanghouchao/MYCMS/issues)

## Contributing & AI Guidelines

- Contributing Guide: see `CONTRIBUTING.md`
- AI submission rules and PR checklist: see `.github/pull_request_template.md` and `.github/copilot-instructions.md`

---

Author: [kanghouchao](https://github.com/kanghouchao)
Repository: [github.com/kanghouchao/MYCMS](https://github.com/kanghouchao/MYCMS)

## Security

This repository enables automated security scanning and dependency updates:

- Static analysis via GitHub CodeQL for Java (backend) and JavaScript/TypeScript (frontend).
- Dependency updates powered by Dependabot for GitHub Actions, npm, and Gradle.

Operational guidance:

- CodeQL runs on pushes to master/releases, PRs targeting master, and on a weekly schedule (Mondays 03:00 UTC); review alerts in the Security tab and address high/critical findings promptly.
- Dependabot opens weekly PRs (grouped for minor/patch where applicable). Prioritize PRs with security advisories, and test in CI before merge.

If a tool or schedule needs adjustment, propose changes via PR and reference the related issue (see issue #12).
