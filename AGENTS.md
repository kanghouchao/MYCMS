# Repository Guidelines

## Project Structure & Module Organization
- `backend/` contains the Spring Boot API (Java 21). Source lives in `src/main/java`, configuration in `src/main/resources`, and domain-aligned tests under `src/test/java` (`config`, `listener`, `service`). Multi-stage Docker builds (`backend/Dockerfile`) provide lint, test, and runtime targets.
- `frontend/` houses the Next.js app (TypeScript). Feature code resides in `src/`, colocated unit specs end with `.test.ts(x)`, and static assets live in `public/`. The Docker recipe mirrors the backend and exposes a standalone Next server.
- `environment/<env>/` offers Docker Compose harnesses (`development`, `release`) with helper Makefiles for `up`, `down`, `logs`, and `exec`. Shared CI artefacts are published to `reports/`.

## Build, Test, and Development Commands
- `make build [service=backend|frontend]` builds Docker images using the staged Dockerfiles.
- `./gradlew bootRun` and `npm run dev` run the API and UI locally; both honour hot reload.
- `make lint` runs Spotless or ESLint via service Makefiles; `make format service=frontend` + `./gradlew spotlessApply` apply fixes.
- `make test [service=…]` executes unit suites inside Docker, copying results to `reports/<service>/`. For direct runs, use `./gradlew test jacocoTestReport` and `npm test -- --coverage`.
- `make up env=development` spins up the full stack with Traefik, PostgreSQL, Redis; `make logs service=backend` tails individual containers.

## Coding Style & Naming Conventions
- Java code follows Google Java Format (Spotless); packages stay lowercase (`com.cms.<module>`), DTO/Request suffixes describe payloads, and Lombok/Spring annotations mirror existing patterns.
- Frontend uses Prettier (2-space, single quotes) plus `eslint.config.mjs`; React components/pages are `PascalCase`, hooks begin with `use`, and service clients belong in `frontend/src/services`.

## Testing Guidelines
- Backend tests run on JUnit with the embedded H2 profile; Jacoco enforces ≥70% line coverage. Run through Docker (`make test service=backend`) or locally (`./gradlew test jacocoTestCoverageVerification`).
- Frontend uses Jest + Testing Library; colocate specs and keep coverage ≥70% to satisfy Jest thresholds.
- Prefer contract tests for middleware and API clients, especially when manipulating tenant routing or cookies.

## Commit & Pull Request Guidelines
- Adhere to Conventional Commits (`type(scope): summary`) under 70 chars. Reference issues with `Fixes #<id>` and capture manual validation (e.g., `make lint`, `make test`, screenshots for UI changes).
- PRs must summarize purpose and impact, list key files touched, link generated reports, and call out follow-up tasks. Keep changes focused; separate infra tweaks from feature work.

## Security, Observability & Configuration
- Start from `environment/docker-compose.example.yml` for secrets; never commit real `.env` files. Spring overrides belong in `application-*.yml` profiles.
- Health probes are exposed at `/actuator/health`, `/actuator/health/liveness`, and `/actuator/health/readiness`; retain DB/Redis checks in readiness.
- Every response propagates `X-Request-ID`; backend logs render `req=<id>` and `tenant=<role>`—include these when adding log lines or middleware.
