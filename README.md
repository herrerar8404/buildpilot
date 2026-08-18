# BuildPilot

BuildPilot is a Spring Boot API with a React/Vite frontend and PostgreSQL database. The repository includes a reproducible local stack and CI checks for both applications.

## Prerequisites

- Docker with Docker Compose (recommended), or
- Java 17+, Maven 3.9+ and Node.js 22+ for native development.

## Run the complete stack

```bash
cp .env.example .env
docker compose up --build
```

The frontend is available at <http://localhost:5173>, the API at <http://localhost:8080/api/v1>, and Swagger UI at <http://localhost:8080/swagger-ui.html>. PostgreSQL data is stored in the `postgres_data` volume. Stop the stack with `docker compose down`; add `--volumes` only when you intentionally want to delete local database data.

All values in `.env.example` are development defaults. Set a non-default `POSTGRES_PASSWORD` outside local development. The Compose frontend proxies `/api` to the backend, so no browser-visible backend URL is required.

## Native development

Start PostgreSQL first (the `db` Compose service can be used independently):

```bash
docker compose up -d db
```

Run the backend:

```bash
sh ./mvnw spring-boot:run
```

On Windows use `mvnw.cmd`. The application reads these environment variables, with local defaults shown:

| Variable | Default |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/buildpilot` |
| `DB_USERNAME` | `buildpilot` |
| `DB_PASSWORD` | `buildpilot` |
| `SERVER_PORT` | `8080` |
| `JPA_SHOW_SQL` | `false` |

Run the frontend in another terminal:

```bash
cd frontend
npm ci
npm run dev
```

Vite proxies `/api` to `VITE_DEV_BACKEND_URL` (default `http://localhost:8080`). `VITE_API_BASE_URL` can instead set a browser-visible API origin at build time.

## Validation

```bash
sh ./mvnw verify
cd frontend && npm ci && npm run build
docker compose config --quiet
docker compose build
```

GitHub Actions performs all four checks on pull requests.

## Database migrations

Flyway is the only schema authority; Hibernate schema generation is disabled. Migrations run automatically when the backend starts. Never edit a migration that has already been applied—add a new versioned migration instead.

## Known domain inconsistency

The current frontend and backend disagree about valid `shapeType`/`wallCount` combinations:

- frontend: `RECTANGLE = 4`, `OPEN_AREA = 2..3`, `IRREGULAR >= 5`;
- backend and migration V8: `OPEN_AREA >= 2`, `RECTANGLE` and `IRREGULAR >= 3` (with defaults of 4 for rectangles and 3 otherwise).

There is not enough repository context to determine the intended business rule. BP-001 therefore preserves existing behavior. A follow-up decision should define one canonical rule, apply it consistently in request validation, mapping, entity defaults, database constraints and UI validation, and include boundary tests.
