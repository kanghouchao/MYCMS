## Oli CMS — AI Coding Agent Guide

This repo is a split-architecture, multi-tenant CMS behind Traefik. Follow these project-specific patterns to be productive.

### Architecture
- Reverse proxy: `/api/*` → backend (prefix stripped), others → frontend (`docker-compose.yml`, `traefik/development/traefik.yml`).
- Frontend: Next.js 14 with middleware-based domain routing (`frontend/src/middleware.ts`).
- Backend: Spring Boot 3.5 (Java 21). Namespace endpoints under `/central/*` (admin) and `/tenant/*` (tenant). Health: `/actuator/health`.

## Oli CMS — AI Agent Quick Guide

This is a concise, actionable guide for AI coding agents to be productive in this repository.
Priorities: 1) follow `.specify/` (Spec Kit) templates, 2) respect the Constitution
(`.specify/memory/constitution.md`) for governance, 3) prefer Makefile targets for local ops.

Architecture quick facts
- Split architecture: `frontend/` (Next.js 14) + `backend/` (Spring Boot). Traefik routes
	requests; frontend API calls go through `/api/*` (proxy strips prefix). See
	`traefik/development/traefik.yml` and `frontend/src/middleware.ts`.

Multi-tenant and cookie contract
- Middleware establishes tenant vs admin by host. Cookies set by middleware include
	`x-mw-role`, `x-mw-tenant-template`, `x-mw-tenant-id`, `x-mw-tenant-name`. Server
	components read via `cookies()` (Next.js server components) and must validate
	tenant context server-side.

Key developer flows (examples)
- Build & run locally: `make build` then `make up` (starts stack via Compose/Traefik).
- Run tests: `make test` or `make test service=backend`.
- Logs: `make logs service=backend|frontend|traefik`.

Project-specific conventions
- Use shared axios client: `frontend/src/lib/client.ts` (baseURL `/api`) — do NOT
	create ad-hoc axios instances.
- Backend controllers: namespace under `com.cms.controller.central` and
	`com.cms.controller.tenant`.
- Tests first (TDD): new features should start with failing tests (unit + integration
	+ contract where applicable); tests are committed before implementation.

Agent operation notes
- Prefer Spec Kit commands and templates in `.specify/` for generating plans/specs/tasks
	(`/plan` uses `plan-template.md`). The Constitution provides policy gates.
- Prefer editor/MCP APIs for GitHub interactions when available; otherwise use
	single-shot shell commands wrapped with `bash -lc 'set -euo pipefail; ...'`.
- For long-running services, prefer `make up` (background) and monitor via `make logs`.

Where to look for examples
- Frontend tenant logic: `frontend/src/middleware.ts`, `frontend/src/app/page.tsx`.
- Shared client: `frontend/src/lib/client.ts`.
- Backend controllers: `backend/src/main/java/com/cms/controller/`.
- Spec Kit templates: `.specify/templates/` and existing specs under `specs/`.

If a required pattern is unclear, create an issue and reference it in your PR; link
the relevant spec/template and request guidance from maintainers.

Feedback request: Review this condensed guide and tell me which sections need more
examples or stricter rules (e.g., commit message format, CI stage names).
