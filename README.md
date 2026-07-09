# Your Portfolio Site

A minimal, feminine, vintage-inspired portfolio site built with **Java + Spring Boot**.
It has three public sections — **Resume**, **Hobbies**, and **Journal (blog)** — plus a
password-protected **Admin Dashboard** where you can edit everything without touching code.

## What's inside

- **Java 17 + Spring Boot 3** backend (Web, Security, Data JPA, Thymeleaf)
- **H2 file-based database** — no separate database server to install; your data lives in
  `./data/portfolio.mv.db` and survives restarts
- **Spring Security** login for `/admin/**`, with a BCrypt-hashed password
- Full **CRUD admin dashboard** for blog posts, hobbies, resume profile, work experience,
  education, and skills
- A custom design system (see `src/main/resources/static/css/style.css`): parchment
  background, dusty rose/sage/wine/gold palette, Cormorant Garamond + EB Garamond serif
  type, and a hand-drawn botanical divider used as the site's signature motif

## Requirements

- **Java 17 or newer** ([adoptium.net](https://adoptium.net) has free installers)
- **Maven** (or use the included `mvnw` wrapper if you add one — see below)

Check your Java version:
```bash
java -version
```

## Running it locally

1. Unzip the project and open a terminal in the project folder.
2. Start the app:
   ```bash
   mvn spring-boot:run
   ```
   (First run will download dependencies — this needs an internet connection once.)
3. Visit **http://localhost:8080** for the public site.
4. Visit **http://localhost:8080/admin/login** for the admin dashboard.

### Default admin login

On first startup, an admin account is created automatically from these settings in
`src/main/resources/application.properties`:

```properties
portfolio.admin.username=admin
portfolio.admin.password=changeme123
```

**Change `portfolio.admin.password` before you deploy this anywhere public,** then delete
the `data/` folder once so the new password takes effect on next startup (it's only used
the very first time the admin account is created).

## Customizing your content

Everything is editable from the admin dashboard — no code changes needed:

- **`/admin/resume`** — your name, tagline, summary, contact links, work experience,
  education, and skills
- **`/admin/hobbies`** — add a hobby with a title, description, and an optional image
  (paste an image URL — e.g. one hosted on Imgur or your own storage)
- **`/admin/blog`** — write, edit, publish/unpublish, or delete journal entries. The
  content field accepts simple HTML tags (`<p>`, `<b>`, `<i>`, `<h2>`, `<a>`, etc.)
- **`/admin/polls`** — set this week's poll question and two answer options. Only one
  poll is "active" (shown on the homepage) at a time. Visitors vote once per poll — a
  vote is tied to the visitor's IP address, enforced by a database uniqueness
  constraint — and immediately see a live pie chart of results.

## Customizing the design

- Colors, fonts, and spacing are all defined as CSS variables at the top of
  `src/main/resources/static/css/style.css` under `:root`. Change a hex value there and
  it updates everywhere.
- The botanical divider (the little sprig graphic between sections) is inline SVG in
  `src/main/resources/templates/fragments/layout.html` — search for `th:fragment="divider"`.
- Page templates are plain Thymeleaf HTML in `src/main/resources/templates/`.

## Moving to a real domain / production database

This ships with H2 for zero-setup local use. For a real deployment:

1. **Switch the database.** In `application.properties`, replace the H2 block with your
   Postgres/MySQL connection details, e.g.:
   ```properties
   spring.datasource.url=jdbc:postgresql://<host>:5432/<db>
   spring.datasource.username=<user>
   spring.datasource.password=<password>
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   ```
   Add the corresponding driver dependency to `pom.xml` (e.g. `org.postgresql:postgresql`).
2. **Turn off the H2 console** — remove or comment out the `spring.h2.console.*` lines;
   they're for local development only.
3. **Package the app** into a runnable jar:
   ```bash
   mvn clean package
   java -jar target/portfolio-site-1.0.0.jar
   ```
4. Deploy that jar to any Java-friendly host (Render, Railway, Fly.io, an EC2 box, etc.)
   and point your domain at it.

## Project structure

```
src/main/java/com/portfolio/app/
├── PortfolioApplication.java     # entry point
├── config/                       # security + startup data seeding
├── model/                        # JPA entities
├── repository/                   # Spring Data repositories
├── service/                      # UserDetailsService for login
└── controller/                   # public pages + admin CRUD
src/main/resources/
├── application.properties
├── templates/                    # Thymeleaf HTML (public + admin)
└── static/css/style.css          # the whole design system
```

## Notes

- Passwords are stored as BCrypt hashes — never in plain text.
- The `/h2-console` path is enabled for local development so you can peek at the
  database directly; disable it before deploying publicly.
- Blog post URLs are auto-generated from the title (e.g. "My First Post" →
  `/blog/my-first-post"`) but you can override the slug manually in the entry form.
- The weekly poll tracks one vote per IP address per poll (enforced at the database
  level). This is a reasonable, low-friction anti-spam measure without requiring user
  accounts, but it's not perfect — people sharing a network (offices, cafes, mobile
  carriers using NAT) share an IP, and VPN users can vote once per exit node.

## Connect to H2 database locally
Go to http://localhost:8080/h2-console
Fill in the login form exactly like this:

JDBC URL: jdbc:h2:file:./data/portfolio;AUTO_SERVER=TRUE
User Name: sa
Password: (leave blank)

Click Connect