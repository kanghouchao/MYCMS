# Repository Guidelines

## Project Structure & Module Organization
- `backend/` hosts the Spring Boot API (Java 21); source lives in `src/main/java`, configs in `src/main/resources`, and targeted tests in `src/test/java` (`config`, `listener`, `service`). Multi-stage builds live in `backend/Dockerfile`.
- `frontend/` contains the Next.js app; feature code sits in `src/`, colocated specs end with `.test.ts(x)`, and static assets reside in `public/`.
- `environment/<env>/` (e.g., `development`, `release`) supplies Docker Compose stacks with helper Makefiles for `up`, `down`, `logs`, and `exec`. CI artefacts collect in `reports/`.

## Build, Test, and Development Commands
- `make build service=backend|frontend` runs the staged Docker build for the chosen service.
- `./gradlew bootRun` (API) and `npm run dev` (UI) start hot-reload servers without Docker.
- `make lint` triggers Spotless or ESLint via service wrappers; apply fixes with `make format service=frontend`.
- `make test service=…` executes unit suites in Docker and copies results to `reports/<service>/`.
- `make up env=development` launches the integrated stack (Traefik, PostgreSQL, Redis); override `env` for other Compose bundles and use `make logs service=backend` to tail containers.
- `make exec env=development service=backend` (or `service=frontend`) opens an interactive `sh` shell inside the running container for debugging; set `env` if you target a different stack.

## Coding Style & Naming Conventions
- Java follows Google Java Format via Spotless; packages stay lowercase (`com.cms.<module>`), DTO/Request suffixes describe payloads, and Lombok/Spring annotations align with existing usage.
- Frontend code is formatted by Prettier (2-space, single quotes) and `eslint.config.mjs`; React components/pages use PascalCase, hooks start with `use`, and shared API clients live in `frontend/src/services`.

## Testing Guidelines
- Backend tests use JUnit on embedded H2 with Jacoco enforcing ≥70% line coverage (`./gradlew test jacocoTestCoverageVerification`).
- Frontend relies on Jest + Testing Library; colocate specs and keep coverage ≥70% using `npm test -- --coverage`.
- Prioritize contract tests for middleware and API clients, especially when handling tenant routing or cookies.

## Commit & Pull Request Guidelines
- Follow Conventional Commits (`type(scope): summary`) under 70 characters; reference issues with `Fixes #<id>` and list manual validation (e.g., `make lint`, screenshots).
- Create every PR using the templates stored in `.github/` (e.g., `.github/pull_request_template.md`); do not submit without applying the appropriate template.
- Pull requests should explain purpose and impact, enumerate key files, attach generated reports, and capture follow-up tasks. Keep scope tight—split infra changes from feature work.

## Security & Configuration Tips
- Base secrets on `environment/docker-compose.example.yml`; never commit real `.env` files. Environment overrides belong in `backend/src/main/resources/application-*.yml`.
- Expose health probes at `/actuator/health*` and retain DB/Redis readiness checks. Include `req=<id>` and `tenant=<role>` in new log statements to preserve traceability.
