# Database & Migrations Usage

The app runs on **PostgreSQL** with **Flyway** managing the schema. Tests run on in-memory **H2**.

## Local setup

```bash
docker compose up -d        # Postgres 17 + Redis (docker-compose.yml)
./mvnw spring-boot:run      # dev profile -> jdbc:postgresql://localhost:5432/appdb (app/app)
```

Override with `DATABASE_URL` / `DATABASE_USERNAME` / `DATABASE_PASSWORD`. A running Postgres is now
required to start the app (H2 is test-only).

## Schema ownership

- **Flyway** owns the schema: migrations in `src/main/resources/db/migration/V*__*.sql` run on startup.
- **Hibernate** is `ddl-auto: validate` in dev & prod — it never mutates the schema, only checks that
  the entities match it. A mismatch fails startup (catches drift).
- **Tests**: `spring.flyway.enabled=false` + `ddl-auto=create-drop` on H2 (Flyway SQL is PG-specific).

## The V1 baseline (auto-generated — don't hand-edit)

`V1__baseline.sql` was generated from the JPA entities, not written by hand:

1. start a throwaway Postgres 17 container;
2. boot the app against it with `SPRING_JPA_HIBERNATE_DDL_AUTO=create SPRING_FLYWAY_ENABLED=false`
   (Hibernate creates every table from the entities — incl. `shedlock`, which is an entity; Quartz
   uses RAMJobStore so there are no quartz tables);
3. `pg_dump --schema-only --no-owner --no-privileges --no-comments`, then strip psql meta-commands
   (`\restrict`, `SET …`, `SELECT pg_catalog.set_config`, comment lines).

**Verify** a baseline by booting against a fresh schema with Flyway on + `ddl-auto=validate`: it must
reach "Started" with no schema-validation errors.

## Adding a change

Hand-write the next migration as `V2__<description>.sql` (then `V3__…`, …) and update the entities to
match. Never edit an applied migration; never go back to `ddl-auto: update`.
