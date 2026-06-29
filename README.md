# Southeast University Games & Sports Club Management System

A production-ready web application for managing Southeast University's Games & Sports
Club: student membership, executive committees, events, news, gallery, achievements,
notices, and email notifications.

> **Build status:** Module 1 — Project Setup ✅

---

## Tech Stack

| Layer        | Technology                                                       |
|--------------|------------------------------------------------------------------|
| Language     | Java 21                                                          |
| Framework    | Spring Boot 3.3.5 (Web, Security, Data JPA, Mail, Validation)    |
| ORM          | Spring Data JPA / Hibernate                                      |
| Database     | MySQL 8                                                          |
| View         | Thymeleaf + HTML5 / CSS3 / Vanilla JS / Bootstrap 5             |
| Build        | Maven 3.9+                                                       |
| Email        | Spring Boot Mail (JavaMailSender / SMTP)                         |

## Architecture

MVC + layered (Controller → Service → Repository → Entity) with DTOs, the
Repository pattern, centralized exception handling, and SOLID, clean-code design.

```
bd.edu.seu.gamesclub
├── config        # @Configuration beans, properties binding, web/mvc setup
├── controller    # Spring MVC controllers (presentation)
├── dto           # Data Transfer Objects + validated form beans
├── entity        # JPA entities (domain model / schema)
├── repository    # Spring Data JPA repositories
├── security      # Spring Security config & components
├── service       # Business-logic contracts
│   └── impl      #   implementations
├── exception     # Custom exceptions + global handler
└── util          # Stateless helpers (OTP, file storage, constants)
```

## Roles

- **ROLE_ADMIN** — seeded manually in the database.
- **ROLE_STUDENT** — self-registers using an official `@seu.edu.bd` email (OTP verified).

---

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL 8 (running locally for the `dev` profile)

## Configuration

Settings live in `src/main/resources`:

- `application.properties` — common settings + `app.*` custom config
- `application-dev.properties` — local development (default profile)
- `application-prod.properties` — production (all secrets via env vars)

Key environment variables:

| Variable        | Purpose                          | Dev default          |
|-----------------|----------------------------------|----------------------|
| `DB_HOST`       | MySQL host                       | `localhost`          |
| `DB_PORT`       | MySQL port                       | `3306`               |
| `DB_NAME`       | Database name                    | `seu_sports_club`    |
| `DB_USERNAME`   | MySQL user                       | `root`               |
| `DB_PASSWORD`   | MySQL password                   | `root`               |
| `MAIL_USERNAME` | SMTP username                    | _(empty)_            |
| `MAIL_PASSWORD` | SMTP password / app password     | _(empty)_            |
| `UPLOAD_DIR`    | Root dir for uploaded images     | `uploads`            |

## Running locally

```bash
# 1. Ensure MySQL is running (DB is auto-created on the dev profile)
# 2. Build & run
mvn spring-boot:run
```

App starts on http://localhost:8080 (profile `dev` by default).

## Build & test

```bash
mvn clean verify      # compile, run tests, package
mvn clean package     # produce target/games-sports-club.jar
java -jar target/games-sports-club.jar --spring.profiles.active=prod
```

> **Note on this sandbox:** the current environment has no access to Maven
> Central, so dependency download / `mvn` build must be run in an
> internet-enabled environment.

---

## Build roadmap (modules)

1. ✅ **Project Setup** — Maven, Spring Boot, config, layered skeleton
2. ⏳ Database Design
3. ⏳ Spring Security
4. ⏳ Authentication (registration, OTP, login, forgot password)
5. ⏳ Landing Page
6. ⏳ Admin Dashboard
7. ⏳ Student Dashboard
8. ⏳ CRUD Modules (sports, committees, events, news, gallery, achievements, notices, contact)
9. ⏳ Email System (OTP, membership notifications, broadcasts)
10. ⏳ Final Testing & Optimization
