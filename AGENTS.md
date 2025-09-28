# Repository Guidelines

## Project Structure & Module Organization
- `backend/` Spring Boot API (Java 21); sources in `src/main/java`, resources in `src/main/resources`.
- `backend/src/test/java` houses domain-aligned suites (`config`, `listener`, `service`).
- `frontend/` Next.js + TypeScript; app code under `src/`, static assets in `public/`.
- Shared outputs accumulate in `reports/`; Docker Compose stacks live in `environment/<env>/` with Traefik routes.

## Build, Test, and Development Commands
- `make build` builds everything; add `service=backend` or `service=frontend` to scope.
- Run `./gradlew bootRun` for the API and `npm run dev` inside `frontend/` for the UI.
- `make lint` triggers Spotless and ESLint; `make format service=frontend` applies Prettier + ESLint fixes.
- `make test` (or `service=<name>`) executes suites and uploads artifacts to `reports/<service>/`.
- Spin up integration stacks via `make up env=development`; stop with `make down env=development`.

## Coding Style & Naming Conventions
- Prettier (2-space indent, single quotes) and rules in `eslint.config.mjs` govern the frontend.
- React components, contexts, and pages use `PascalCase`; hooks start with `use` and live in `frontend/src/hooks`.
- Spotless enforces Google Java Format; keep Lombok annotations and Spring stereotypes consistent.
- Java packages remain lowercase (`com.cms.<module>`), and payload types end with `Dto` or `Request`.

## Testing Guidelines
- Frontend tests run with Jest + Testing Library (`npm run test` or `make test service=frontend`); store specs as `*.test.tsx` beside the unit.
- Backend tests use JUnit Platform and the bundled H2 profile (`./gradlew test`).
- Jacoco requires ≥70% line coverage; expand suites whenever production logic changes.
- Do not commit build outputs from `frontend/coverage` or `backend/build`.

## Commit & Pull Request Guidelines
- Follow Conventional Commits (`type(scope): summary`), e.g., `fix(auth): tighten token expiry`.
- Keep subjects under 70 characters and record behavioural notes or migrations in the body.
- Reference issues with `Fixes #<id>` when applicable and mention manual test steps.
- PRs should detail what changed, why, UI screenshots when relevant, and confirmation that `make lint` + `make test` passed.

## Security & Configuration Tips
- Use `environment/docker-compose.example.yml` as the template for secrets; never commit real `.env` files.
- Update Traefik rules under `environment/*/traefik` when exposing ports, and avoid opening extras.
- Keep sensitive Spring settings in `application-*.yml` overrides, not hard-coded constants.
- Coordinate authentication tweaks with security owners and log final decisions in `SECURITY.md`.
