# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A personal portfolio site (Resume, Hobbies, Journal/blog) with a password-protected Admin Dashboard for editing all content. Java 17 + Spring Boot 3 (Web, Security, Data JPA, Thymeleaf), H2 file-based database, no frontend build step — server-rendered Thymeleaf templates + plain CSS/JS.

## Commands

```bash
mvn spring-boot:run          # run the app locally (http://localhost:8080)
mvn clean package            # build the runnable jar (target/portfolio-site-1.0.0.jar)
java -jar target/portfolio-site-1.0.0.jar
mvn test                     # run tests (no test sources currently exist in src/test)
```

There is no `mvnw` wrapper committed — `mvn` must be installed locally. First run downloads dependencies and requires internet access.

Admin login: `http://localhost:8080/admin/login`, credentials from `portfolio.admin.username`/`portfolio.admin.password` in `application.properties` (only applied the first time the admin user is created — delete `data/` to re-seed after changing the password).

H2 console (dev only): `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:file:./data/portfolio;AUTO_SERVER=TRUE`, user `sa`, blank password.

## Architecture

Standard Spring MVC layering under `src/main/java/com/portfolio/app/`:

- `controller/` — public read-only pages (`PublicController`, `PollController`) and admin CRUD controllers (`Admin*Controller`, one per entity area: blog, hobby, resume, poll, dashboard). Admin controllers follow a consistent list/new/edit/save/delete pattern per entity; `save` handles both create and update via a nullable `id` on the model.
- `model/` — JPA entities (`BlogPost`, `Hobby`, `ResumeProfile`, `Experience`, `Education`, `Skill`, `WeeklyPoll`, `PollResponse`, `User`).
- `repository/` — Spring Data JPA interfaces, one per entity.
- `service/` — business logic that doesn't belong in a controller: `PollService` (voting/results), `UserDetailsServiceImpl` (Spring Security auth).
- `config/` — `SecurityConfig` (form login, `/admin/**` gated behind `ROLE_ADMIN`, everything else public) and `DataInitializer` (a `CommandLineRunner` that seeds the admin user and placeholder content on first boot — checks `repository.count() == 0` per entity, so it's idempotent).
- `util/IpUtil` — best-effort client IP resolution (`X-Forwarded-For` → `X-Real-IP` → `getRemoteAddr()`), used only for poll vote deduplication.
- `dto/`, `exception/` — `PollResultsResponse` and `AlreadyVotedException` supporting the poll feature.

Templates live in `src/main/resources/templates/` (Thymeleaf HTML), split into public pages at the top level and `admin/` for dashboard forms/lists. Shared markup (nav, footer, the botanical divider SVG) is in `templates/fragments/layout.html`. Static assets are in `src/main/resources/static/` (`css/style.css` is the entire design system, driven by CSS custom properties under `:root`; `js/poll.js` handles the homepage poll widget).

### Notable patterns

- **Blog slugs**: auto-generated from the title via `AdminBlogController#slugify` (strips diacritics/punctuation, lowercases, hyphenates) unless a slug is supplied manually; collisions get a timestamp suffix.
- **Poll voting**: one vote per IP per poll, enforced both in `PollService.submitVote` (pre-check) and at the DB level via a unique constraint on `(poll_id, ip_address)` in `PollResponse` — a `DataIntegrityViolationException` from a race is caught and converted to `AlreadyVotedException`. Only one `WeeklyPoll` is `active` at a time.
- **DataInitializer** re-runs every startup but only writes when a repository is empty — safe to leave in place, don't treat it as a one-time migration script.
- **Database**: H2 file DB at `./data/portfolio.mv.db` (gitignored). `spring.jpa.hibernate.ddl-auto=update` auto-migrates the schema from entities — no separate migration files. Switching to Postgres/MySQL means replacing the datasource block in `application.properties` and adding the driver dependency to `pom.xml`.
- **No JS framework / no build step**: admin dashboard interactivity is plain server-rendered forms; `poll.js` is the only custom client-side script.
