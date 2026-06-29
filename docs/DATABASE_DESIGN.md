# Module 2 — Database Design (v2)

**Project:** Southeast University Games & Sports Club Management System
**Engine:** MySQL 8 (InnoDB, `utf8mb4` / `utf8mb4_unicode_ci`)
**Normalization:** 3NF — no transitive dependencies, no duplicated data (audit/log
snapshots excepted, by design).
**Scale target:** designed and indexed for **10,000+** registered students (see §10).
**Status:** For review — no JPA entities are written yet.

> **What changed in v2** (per the additional CMS/management requirements):
> added `club_settings`, `system_settings`, `hero_sliders`, `sponsors`,
> `training_schedules`, `faqs`, `social_links`, and a **central `media_assets`**
> table; introduced full audit columns (`created_by`, `updated_by`,
> `created_at`, `updated_at`) on every business table; added a scalability
> section. **25 tables total.**

---

## 1. Design Decisions (read first)

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Primary key type | `BIGINT UNSIGNED AUTO_INCREMENT` everywhere | Consistent, compact, sequential → best index/FK performance at 10k+ rows. |
| Admin vs Student | One `users` table + `role`; student data in 1:1 `student_profiles` | No duplicate auth columns. |
| Exec vs Sub-Exec | One `committee_members` + `committee_type` discriminator | Identical structure. |
| Enumerations | `VARCHAR` + `CHECK`, mapped `@Enumerated(STRING)` | Portable, readable, no ordinal bug. |
| `@seu.edu.bd` rule | Service/validation layer | Keeps manually-seeded admins flexible. |
| Membership | `membership_periods` + `membership_applications` | History + derived "member" status (no `is_open`/`is_member` flags). |
| OTP | Store a **hash** of the code | DB breach must not leak live OTPs. |
| **Uploaded images (NEW)** | **Central `media_assets` table**; other tables hold a `*_media_id` FK | Satisfies "every image stores alt text / file size / MIME type" in **one** place; avoids repeating those 3 columns across ~12 tables; enables reuse, dedup, and a single media library in the admin. |
| **Social URLs (NEW)** | **Single source of truth = `social_links`**; **removed from `club_settings`** | Storing the same URLs in two tables violates no-duplicate-data. `social_links` is dynamic/extensible (admin can add any platform). ⚠️ *Confirm: keep social only here, or also duplicate in club_settings?* |
| **Club vs System settings (NEW)** | Two singleton tables | `club_settings` = organisation identity/contact; `system_settings` = application/site technical config. Different concerns, different edit screens. |
| **Audit columns (NEW)** | `created_by`,`updated_by` (FK→users, `SET NULL`) + `created_at`,`updated_at` on every business table | Full accountability. Implemented via a shared `Auditable` `@MappedSuperclass`. Pure log/transient tables are exempt (see note). |
| Singletons | `club_settings`, `system_settings` constrained to a single row (`id = 1`) | One global config row, editable from the dashboard. |

**Audit applies to ALL entities (v2.1):** per requirement, **every** entity
extends the common `Auditable` base class and therefore carries the full audit
block (`created_by`, `updated_by`, `created_at`, `updated_at`). The former
"log" tables (`otp_tokens`, `email_broadcast_recipients`, `activity_logs`) now
include it too. For `activity_logs` the acting admin **is** `created_by`, so the
previously separate `actor_user_id` column is removed (no duplicate user column).
`membership_applications` additionally keeps its domain actors `student_user_id`
(applicant) and `reviewed_by` (reviewer).

---

## 2. Entity / Table List (25 tables)

| # | Table | Module | Purpose |
|---|-------|--------|---------|
| 1 | `users` | Auth | Credentials + role |
| 2 | `student_profiles` | Student Profile | 1:1 student data |
| 3 | `membership_periods` | Membership | Open/close drives |
| 4 | `membership_applications` | Membership | Application history |
| 5 | `committee_members` | Committees | Exec + Sub-Exec |
| 6 | `sports` | Sports | Default + custom sports |
| 7 | `events` | Events | Event listings |
| 8 | `news` | News | Articles |
| 9 | `gallery_categories` | Gallery | Albums |
| 10 | `gallery_images` | Gallery | Images per album |
| 11 | `achievements` | Achievements | Accomplishments |
| 12 | `notices` | Notice Board | Notices |
| 13 | `contact_messages` | Contact | Visitor messages |
| 14 | `email_broadcasts` | Email | Campaign header |
| 15 | `email_broadcast_recipients` | Email | Per-recipient log |
| 16 | `otp_tokens` | Auth | Hashed OTPs |
| 17 | `activity_logs` | Activity Log | Admin audit trail |
| 18 | **`media_assets`** | CMS / Media | Central upload store (alt, size, MIME) |
| 19 | **`club_settings`** | CMS | Club identity/contact (singleton) |
| 20 | **`system_settings`** | CMS | Site/app config (singleton) |
| 21 | **`hero_sliders`** | CMS / Landing | Dynamic hero slides |
| 22 | **`sponsors`** | CMS / Landing | Sponsor logos |
| 23 | **`training_schedules`** | CMS | Per-sport practice schedule |
| 24 | **`faqs`** | CMS | FAQ entries |
| 25 | **`social_links`** | CMS | All social media URLs |

---

## 3. ER Diagram (text / crow's-foot)

Legend: `1` one · `N` many · `(opt)` nullable FK.

```
                              ┌─────────────────────┐
                              │        users        │◄────────────────────────┐
                              │ PK id, UQ email      │  created_by/updated_by  │
                              └─────────────────────┘  (SET NULL) from nearly  │
       ┌──────────┬───────────────┬───────────┬────────────┐   every table ───┘
      1:1        1:N             1:N         1:N           1:N
       ▼          ▼               ▼           ▼             ▼
 student_     membership_     activity_   email_       (events, news, notices,
 profiles     applications    logs        broadcasts    sports, committees, ... )
 (UQ user_id,       ▲ N:1                     │ 1:N
  UQ student_id)    │                         ▼
            membership_periods        email_broadcast_recipients

 ┌──────────────────────────── media_assets (PK id) ───────────────────────────┐
 │  Referenced by *_media_id FK (ON DELETE SET NULL) from:                      │
 │   student_profiles.profile_media_id     committee_members.photo_media_id     │
 │   sports.icon_media_id / image_media_id  events.banner_media_id              │
 │   news.image_media_id                    achievements.image_media_id         │
 │   gallery_images.media_id (NOT NULL)     club_settings.logo_media_id         │
 │   system_settings.favicon_media_id       hero_sliders.background_media_id    │
 │   sponsors.logo_media_id                                                     │
 └──────────────────────────────────────────────────────────────────────────────┘

 sports ──1:N──> training_schedules        gallery_categories ──1:N──> gallery_images
 sports ──1:N(opt)──> events               sports ──1:N(opt)──> achievements

 Singletons:  club_settings (id=1)   ·   system_settings (id=1)
 Independent CMS lists:  hero_sliders · sponsors · faqs · social_links
 Standalone:  committee_members · contact_messages · otp_tokens
```

### Relationship summary

| Parent | Child | Type | FK column | On delete |
|--------|-------|------|-----------|-----------|
| users | student_profiles | 1:1 | `user_id` (UQ) | CASCADE |
| users | membership_applications | 1:N | `student_user_id` / `reviewed_by` | CASCADE / SET NULL |
| membership_periods | membership_applications | 1:N | `membership_period_id` | CASCADE |
| sports | events / achievements | 1:N (opt) | `sport_id` | SET NULL |
| sports | training_schedules | 1:N | `sport_id` | CASCADE |
| gallery_categories | gallery_images | 1:N | `category_id` | CASCADE |
| email_broadcasts | email_broadcast_recipients | 1:N | `broadcast_id` | CASCADE |
| **media_assets** | many tables | 1:N (opt) | `*_media_id` | SET NULL (gallery_images: RESTRICT) |
| users | *(audit)* every business table | 1:N | `created_by` / `updated_by` | SET NULL |

---

## 4. SQL Naming Convention

| Element | Convention | Example |
|---------|-----------|---------|
| Table | `snake_case`, plural | `training_schedules` |
| Column | `snake_case` | `start_time`, `theme_color` |
| Primary key | `id` | `id` |
| Foreign key | `<referenced_singular>_id` | `sport_id`, `logo_media_id` |
| Boolean | `is_<adjective>` | `is_active`, `is_maintenance_mode` |
| Timestamps / actors | `created_at`,`updated_at`,`created_by`,`updated_by` | — |
| Unique / FK / Check / Index | `uq_` / `fk_` / `chk_` / `idx_` | `uq_sports_name` |

Enumerated string values use `UPPER_SNAKE_CASE` (`ROLE_ADMIN`, `SUB_EXECUTIVE`, `MONDAY`).

**Standard audit block** appended to every business table:
```sql
    created_by BIGINT UNSIGNED NULL,
    updated_by BIGINT UNSIGNED NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- + FKs: fk_<table>_created_by / fk_<table>_updated_by -> users(id) ON DELETE SET NULL
```

---

## 5. Complete Table Structure (DDL)

> Create order respects FK dependencies: `users` → `media_assets` → everything
> else. All tables `ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci`.
> InnoDB auto-indexes FK columns (`created_by`, `updated_by`, `*_media_id`, ...).

```sql
-- ============================================================
-- 1. users
-- ============================================================
CREATE TABLE users (
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email             VARCHAR(150)    NOT NULL,
    password_hash     VARCHAR(100)    NOT NULL,           -- BCrypt
    role              VARCHAR(20)     NOT NULL,           -- ROLE_ADMIN | ROLE_STUDENT
    is_email_verified TINYINT(1)      NOT NULL DEFAULT 0,
    is_enabled        TINYINT(1)      NOT NULL DEFAULT 1,
    last_login_at     DATETIME        NULL,
    created_by        BIGINT UNSIGNED NULL,
    updated_by        BIGINT UNSIGNED NULL,
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('ROLE_ADMIN','ROLE_STUDENT')),
    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_users_role ON users (role);

-- ============================================================
-- 18. media_assets  (central upload store)  [created early for FK refs]
-- ============================================================
CREATE TABLE media_assets (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    file_path     VARCHAR(255)    NOT NULL,               -- relative path under /uploads
    original_name VARCHAR(255)    NULL,
    alt_text      VARCHAR(255)    NULL,                   -- accessibility / SEO
    mime_type     VARCHAR(100)    NOT NULL,               -- e.g. image/png
    file_size     BIGINT UNSIGNED NOT NULL,               -- bytes
    width         INT             NULL,
    height        INT             NULL,
    media_type    VARCHAR(20)     NOT NULL DEFAULT 'IMAGE', -- IMAGE | DOCUMENT | OTHER
    created_by    BIGINT UNSIGNED NULL,
    updated_by    BIGINT UNSIGNED NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_media_type CHECK (media_type IN ('IMAGE','DOCUMENT','OTHER')),
    CONSTRAINT fk_media_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_media_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_media_type ON media_assets (media_type);

-- ============================================================
-- 2. student_profiles
-- ============================================================
CREATE TABLE student_profiles (
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id          BIGINT UNSIGNED NOT NULL,
    full_name        VARCHAR(120)    NOT NULL,
    student_id       VARCHAR(30)     NOT NULL,
    department       VARCHAR(100)    NOT NULL,
    batch            VARCHAR(20)     NOT NULL,
    semester         VARCHAR(20)     NOT NULL,
    phone            VARCHAR(20)     NOT NULL,
    gender           VARCHAR(10)     NOT NULL,            -- MALE | FEMALE | OTHER
    profile_media_id BIGINT UNSIGNED NULL,               -- -> media_assets
    is_active        TINYINT(1)      NOT NULL DEFAULT 1,
    created_by       BIGINT UNSIGNED NULL,
    updated_by       BIGINT UNSIGNED NULL,
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_student_profiles_user       UNIQUE (user_id),
    CONSTRAINT uq_student_profiles_student_id UNIQUE (student_id),
    CONSTRAINT fk_student_profiles_user  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_profiles_media FOREIGN KEY (profile_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_student_profiles_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_student_profiles_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_student_profiles_gender CHECK (gender IN ('MALE','FEMALE','OTHER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_student_profiles_department ON student_profiles (department);
CREATE INDEX idx_student_profiles_batch      ON student_profiles (batch);

-- ============================================================
-- 3. membership_periods
-- ============================================================
CREATE TABLE membership_periods (
    id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title                VARCHAR(120)    NOT NULL,
    announcement         TEXT            NULL,
    opening_date         DATE            NOT NULL,
    closing_date         DATE            NOT NULL,
    status               VARCHAR(20)     NOT NULL DEFAULT 'DRAFT', -- DRAFT|OPEN|CLOSED
    opened_at            DATETIME        NULL,
    closed_at            DATETIME        NULL,
    is_notification_sent TINYINT(1)      NOT NULL DEFAULT 0,
    created_by           BIGINT UNSIGNED NULL,
    updated_by           BIGINT UNSIGNED NULL,
    created_at           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_membership_periods_status CHECK (status IN ('DRAFT','OPEN','CLOSED')),
    CONSTRAINT chk_membership_periods_dates  CHECK (closing_date >= opening_date),
    CONSTRAINT fk_membership_periods_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_membership_periods_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_membership_periods_status ON membership_periods (status);
CREATE INDEX idx_membership_periods_dates  ON membership_periods (opening_date, closing_date);

-- ============================================================
-- 4. membership_applications
-- ============================================================
CREATE TABLE membership_applications (
    id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    membership_period_id BIGINT UNSIGNED NOT NULL,
    student_user_id      BIGINT UNSIGNED NOT NULL,        -- natural "created_by"
    status               VARCHAR(20)     NOT NULL DEFAULT 'PENDING', -- PENDING|APPROVED|REJECTED|WITHDRAWN
    applied_at           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_by          BIGINT UNSIGNED NULL,            -- natural "updated_by"
    reviewed_at          DATETIME        NULL,
    remarks              VARCHAR(500)    NULL,
    created_at           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_membership_application UNIQUE (membership_period_id, student_user_id),
    CONSTRAINT fk_membership_app_period   FOREIGN KEY (membership_period_id) REFERENCES membership_periods(id) ON DELETE CASCADE,
    CONSTRAINT fk_membership_app_student  FOREIGN KEY (student_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_membership_app_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_membership_app_status  CHECK (status IN ('PENDING','APPROVED','REJECTED','WITHDRAWN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_membership_app_student ON membership_applications (student_user_id);
CREATE INDEX idx_membership_app_status  ON membership_applications (status);

-- ============================================================
-- 5. committee_members  (Executive + Sub-Executive)
-- ============================================================
CREATE TABLE committee_members (
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    committee_type VARCHAR(20)     NOT NULL,              -- EXECUTIVE | SUB_EXECUTIVE
    name           VARCHAR(120)    NOT NULL,
    department     VARCHAR(100)    NOT NULL,
    batch          VARCHAR(20)     NOT NULL,
    position       VARCHAR(80)     NOT NULL,
    photo_media_id BIGINT UNSIGNED NULL,
    facebook_url   VARCHAR(255)    NULL,
    linkedin_url   VARCHAR(255)    NULL,
    session_year   VARCHAR(20)     NULL,
    display_order  INT             NOT NULL DEFAULT 0,
    is_active      TINYINT(1)      NOT NULL DEFAULT 1,
    created_by     BIGINT UNSIGNED NULL,
    updated_by     BIGINT UNSIGNED NULL,
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_committee_type CHECK (committee_type IN ('EXECUTIVE','SUB_EXECUTIVE')),
    CONSTRAINT fk_committee_photo      FOREIGN KEY (photo_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_committee_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_committee_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_committee_type_order ON committee_members (committee_type, display_order);

-- ============================================================
-- 6. sports
-- ============================================================
CREATE TABLE sports (
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name           VARCHAR(80)     NOT NULL,
    slug           VARCHAR(100)    NOT NULL,
    description    TEXT            NULL,
    icon_media_id  BIGINT UNSIGNED NULL,
    image_media_id BIGINT UNSIGNED NULL,
    display_order  INT             NOT NULL DEFAULT 0,
    is_active      TINYINT(1)      NOT NULL DEFAULT 1,
    created_by     BIGINT UNSIGNED NULL,
    updated_by     BIGINT UNSIGNED NULL,
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_sports_name UNIQUE (name),
    CONSTRAINT uq_sports_slug UNIQUE (slug),
    CONSTRAINT fk_sports_icon       FOREIGN KEY (icon_media_id)  REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_sports_image      FOREIGN KEY (image_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_sports_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_sports_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 7. events
-- ============================================================
CREATE TABLE events (
    id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title                 VARCHAR(160)    NOT NULL,
    slug                  VARCHAR(180)    NOT NULL,
    description           TEXT            NULL,
    banner_media_id       BIGINT UNSIGNED NULL,
    venue                 VARCHAR(160)    NULL,
    event_date            DATE            NOT NULL,
    event_time            TIME            NULL,
    registration_deadline DATETIME        NULL,
    status                VARCHAR(20)     NOT NULL DEFAULT 'UPCOMING', -- UPCOMING|ONGOING|COMPLETED|CANCELLED
    sport_id              BIGINT UNSIGNED NULL,
    created_by            BIGINT UNSIGNED NULL,
    updated_by            BIGINT UNSIGNED NULL,
    created_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_events_slug UNIQUE (slug),
    CONSTRAINT fk_events_banner     FOREIGN KEY (banner_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_events_sport      FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE SET NULL,
    CONSTRAINT fk_events_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_events_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_events_status CHECK (status IN ('UPCOMING','ONGOING','COMPLETED','CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_events_event_date ON events (event_date);
CREATE INDEX idx_events_status     ON events (status);

-- ============================================================
-- 8. news
-- ============================================================
CREATE TABLE news (
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title          VARCHAR(180)    NOT NULL,
    slug           VARCHAR(200)    NOT NULL,
    content        TEXT            NOT NULL,
    image_media_id BIGINT UNSIGNED NULL,
    publish_date   DATE            NULL,
    status         VARCHAR(20)     NOT NULL DEFAULT 'DRAFT', -- DRAFT | PUBLISHED
    created_by     BIGINT UNSIGNED NULL,
    updated_by     BIGINT UNSIGNED NULL,
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_news_slug UNIQUE (slug),
    CONSTRAINT fk_news_image      FOREIGN KEY (image_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_news_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_news_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_news_status CHECK (status IN ('DRAFT','PUBLISHED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_news_publish_date ON news (publish_date);
CREATE INDEX idx_news_status       ON news (status);

-- ============================================================
-- 9. gallery_categories
-- ============================================================
CREATE TABLE gallery_categories (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)    NOT NULL,
    slug        VARCHAR(120)    NOT NULL,
    description VARCHAR(255)    NULL,
    created_by  BIGINT UNSIGNED NULL,
    updated_by  BIGINT UNSIGNED NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_gallery_categories_name UNIQUE (name),
    CONSTRAINT uq_gallery_categories_slug UNIQUE (slug),
    CONSTRAINT fk_gallery_cat_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_gallery_cat_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 10. gallery_images  (many per category; image lives in media_assets)
-- ============================================================
CREATE TABLE gallery_images (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    category_id   BIGINT UNSIGNED NOT NULL,
    media_id      BIGINT UNSIGNED NOT NULL,
    caption       VARCHAR(255)    NULL,
    display_order INT             NOT NULL DEFAULT 0,
    created_by    BIGINT UNSIGNED NULL,
    updated_by    BIGINT UNSIGNED NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_gallery_images_category   FOREIGN KEY (category_id) REFERENCES gallery_categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_gallery_images_media      FOREIGN KEY (media_id) REFERENCES media_assets(id) ON DELETE RESTRICT,
    CONSTRAINT fk_gallery_images_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_gallery_images_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_gallery_images_category ON gallery_images (category_id, display_order);

-- ============================================================
-- 11. achievements
-- ============================================================
CREATE TABLE achievements (
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title            VARCHAR(180)    NOT NULL,
    achievement_year SMALLINT        NOT NULL,
    description      TEXT            NULL,
    image_media_id   BIGINT UNSIGNED NULL,
    sport_id         BIGINT UNSIGNED NULL,
    display_order    INT             NOT NULL DEFAULT 0,
    created_by       BIGINT UNSIGNED NULL,
    updated_by       BIGINT UNSIGNED NULL,
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_achievements_image      FOREIGN KEY (image_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_achievements_sport      FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE SET NULL,
    CONSTRAINT fk_achievements_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_achievements_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_achievements_year CHECK (achievement_year BETWEEN 1990 AND 2100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_achievements_year ON achievements (achievement_year);

-- ============================================================
-- 12. notices
-- ============================================================
CREATE TABLE notices (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title        VARCHAR(180)    NOT NULL,
    content      TEXT            NOT NULL,
    notice_type  VARCHAR(20)     NOT NULL DEFAULT 'GENERAL', -- PRACTICE|TOURNAMENT|HOLIDAY|GENERAL
    publish_date DATE            NULL,
    expiry_date  DATE            NULL,
    is_pinned    TINYINT(1)      NOT NULL DEFAULT 0,
    is_published TINYINT(1)      NOT NULL DEFAULT 1,
    created_by   BIGINT UNSIGNED NULL,
    updated_by   BIGINT UNSIGNED NULL,
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_notices_type CHECK (notice_type IN ('PRACTICE','TOURNAMENT','HOLIDAY','GENERAL')),
    CONSTRAINT fk_notices_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_notices_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_notices_type         ON notices (notice_type);
CREATE INDEX idx_notices_publish_date ON notices (publish_date);

-- ============================================================
-- 13. contact_messages
-- ============================================================
CREATE TABLE contact_messages (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name       VARCHAR(120)    NOT NULL,
    email      VARCHAR(150)    NOT NULL,
    subject    VARCHAR(180)    NULL,
    message    TEXT            NOT NULL,
    is_read    TINYINT(1)      NOT NULL DEFAULT 0,
    ip_address VARCHAR(45)     NULL,
    created_by BIGINT UNSIGNED NULL,                      -- NULL = anonymous visitor
    updated_by BIGINT UNSIGNED NULL,                      -- admin who handled it
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_contact_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_contact_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_contact_messages_is_read ON contact_messages (is_read);

-- ============================================================
-- 14. email_broadcasts  (created_by = sender)
-- ============================================================
CREATE TABLE email_broadcasts (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    subject         VARCHAR(200)    NOT NULL,
    body            LONGTEXT        NOT NULL,
    target_type     VARCHAR(20)     NOT NULL,             -- ALL_STUDENTS|MEMBERS_ONLY|SELECTED
    recipient_count INT             NOT NULL DEFAULT 0,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING', -- PENDING|SENT|PARTIAL|FAILED
    sent_at         DATETIME        NULL,
    created_by      BIGINT UNSIGNED NULL,                 -- the sending admin
    updated_by      BIGINT UNSIGNED NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_email_broadcasts_target CHECK (target_type IN ('ALL_STUDENTS','MEMBERS_ONLY','SELECTED')),
    CONSTRAINT chk_email_broadcasts_status CHECK (status IN ('PENDING','SENT','PARTIAL','FAILED')),
    CONSTRAINT fk_email_broadcasts_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_email_broadcasts_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_email_broadcasts_sent_at ON email_broadcasts (sent_at);

-- ============================================================
-- 15. email_broadcast_recipients  (delivery log; extends Auditable)
-- ============================================================
CREATE TABLE email_broadcast_recipients (
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    broadcast_id      BIGINT UNSIGNED NOT NULL,
    recipient_user_id BIGINT UNSIGNED NULL,
    recipient_email   VARCHAR(150)    NOT NULL,
    delivery_status   VARCHAR(20)     NOT NULL DEFAULT 'PENDING', -- PENDING|SENT|FAILED
    error_message     VARCHAR(255)    NULL,
    sent_at           DATETIME        NULL,
    created_by        BIGINT UNSIGNED NULL,
    updated_by        BIGINT UNSIGNED NULL,
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_broadcast_recipient UNIQUE (broadcast_id, recipient_user_id),
    CONSTRAINT fk_broadcast_recipients_broadcast FOREIGN KEY (broadcast_id) REFERENCES email_broadcasts(id) ON DELETE CASCADE,
    CONSTRAINT fk_broadcast_recipients_user      FOREIGN KEY (recipient_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_broadcast_recipients_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_broadcast_recipients_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_broadcast_recipient_status CHECK (delivery_status IN ('PENDING','SENT','FAILED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_broadcast_recipients_broadcast ON email_broadcast_recipients (broadcast_id);

-- ============================================================
-- 16. otp_tokens  (hashed; extends Auditable)
-- ============================================================
CREATE TABLE otp_tokens (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email         VARCHAR(150)    NOT NULL,
    otp_hash      VARCHAR(100)    NOT NULL,
    purpose       VARCHAR(20)     NOT NULL,               -- REGISTRATION | FORGOT_PASSWORD
    expires_at    DATETIME        NOT NULL,
    is_used       TINYINT(1)      NOT NULL DEFAULT 0,
    used_at       DATETIME        NULL,
    attempt_count INT             NOT NULL DEFAULT 0,
    last_sent_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT UNSIGNED NULL,
    updated_by    BIGINT UNSIGNED NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_otp_purpose CHECK (purpose IN ('REGISTRATION','FORGOT_PASSWORD')),
    CONSTRAINT fk_otp_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_otp_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_otp_email_purpose ON otp_tokens (email, purpose);
CREATE INDEX idx_otp_expires_at    ON otp_tokens (expires_at);

-- ============================================================
-- 17. activity_logs  (extends Auditable; created_by = actor)
-- ============================================================
CREATE TABLE activity_logs (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    action        VARCHAR(100)    NOT NULL,
    entity_type   VARCHAR(60)     NULL,
    entity_id     BIGINT UNSIGNED NULL,
    description   VARCHAR(500)    NULL,
    ip_address    VARCHAR(45)     NULL,
    user_agent    VARCHAR(255)    NULL,
    created_by    BIGINT UNSIGNED NULL,                   -- the acting admin (auditor)
    updated_by    BIGINT UNSIGNED NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_activity_logs_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_activity_logs_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_activity_logs_created_by ON activity_logs (created_by);
CREATE INDEX idx_activity_logs_created_at ON activity_logs (created_at);
CREATE INDEX idx_activity_logs_entity     ON activity_logs (entity_type, entity_id);

-- ============================================================
-- 19. club_settings  (SINGLETON: id = 1)
-- ============================================================
CREATE TABLE club_settings (
    id               BIGINT UNSIGNED NOT NULL DEFAULT 1,
    university_name  VARCHAR(160)    NOT NULL,
    club_name        VARCHAR(160)    NOT NULL,
    logo_media_id    BIGINT UNSIGNED NULL,
    established_year SMALLINT        NULL,
    motto            VARCHAR(255)    NULL,
    about_club       TEXT            NULL,
    address          VARCHAR(255)    NULL,
    phone            VARCHAR(40)     NULL,
    email            VARCHAR(150)    NULL,
    google_map_link  VARCHAR(500)    NULL,
    created_by       BIGINT UNSIGNED NULL,
    updated_by       BIGINT UNSIGNED NULL,
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_club_settings_singleton CHECK (id = 1),
    CONSTRAINT chk_club_settings_year CHECK (established_year IS NULL OR established_year BETWEEN 1900 AND 2100),
    CONSTRAINT fk_club_settings_logo       FOREIGN KEY (logo_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_club_settings_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_club_settings_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- NOTE: Facebook/Instagram/LinkedIn/YouTube live in social_links (single source of truth).

-- ============================================================
-- 20. system_settings  (SINGLETON: id = 1)
-- ============================================================
CREATE TABLE system_settings (
    id                  BIGINT UNSIGNED NOT NULL DEFAULT 1,
    site_title          VARCHAR(160)    NOT NULL,
    favicon_media_id    BIGINT UNSIGNED NULL,
    theme_color         VARCHAR(20)     NULL,             -- hex e.g. #0F766E
    footer_copyright    VARCHAR(255)    NULL,
    smtp_sender_name    VARCHAR(120)    NULL,
    is_maintenance_mode TINYINT(1)      NOT NULL DEFAULT 0,
    created_by          BIGINT UNSIGNED NULL,
    updated_by          BIGINT UNSIGNED NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_system_settings_singleton CHECK (id = 1),
    CONSTRAINT fk_system_settings_favicon    FOREIGN KEY (favicon_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_system_settings_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_system_settings_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 21. hero_sliders  (dynamic landing hero)
-- ============================================================
CREATE TABLE hero_sliders (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title               VARCHAR(180)    NULL,
    subtitle            VARCHAR(220)    NULL,
    description         TEXT            NULL,
    button_text         VARCHAR(60)     NULL,
    button_url          VARCHAR(255)    NULL,
    background_media_id BIGINT UNSIGNED NULL,
    display_order       INT             NOT NULL DEFAULT 0,
    is_active           TINYINT(1)      NOT NULL DEFAULT 1,
    created_by          BIGINT UNSIGNED NULL,
    updated_by          BIGINT UNSIGNED NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_hero_background FOREIGN KEY (background_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_hero_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_hero_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_hero_active_order ON hero_sliders (is_active, display_order);

-- ============================================================
-- 22. sponsors
-- ============================================================
CREATE TABLE sponsors (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name          VARCHAR(120)    NOT NULL,
    logo_media_id BIGINT UNSIGNED NULL,
    website       VARCHAR(255)    NULL,
    display_order INT             NOT NULL DEFAULT 0,
    is_active     TINYINT(1)      NOT NULL DEFAULT 1,
    created_by    BIGINT UNSIGNED NULL,
    updated_by    BIGINT UNSIGNED NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_sponsors_logo       FOREIGN KEY (logo_media_id) REFERENCES media_assets(id) ON DELETE SET NULL,
    CONSTRAINT fk_sponsors_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_sponsors_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_sponsors_active_order ON sponsors (is_active, display_order);

-- ============================================================
-- 23. training_schedules
-- ============================================================
CREATE TABLE training_schedules (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    sport_id    BIGINT UNSIGNED NOT NULL,
    day_of_week VARCHAR(10)     NOT NULL,                 -- MONDAY..SUNDAY
    start_time  TIME            NOT NULL,
    end_time    TIME            NOT NULL,
    venue       VARCHAR(160)    NULL,
    coach_name  VARCHAR(120)    NULL,
    is_active   TINYINT(1)      NOT NULL DEFAULT 1,
    created_by  BIGINT UNSIGNED NULL,
    updated_by  BIGINT UNSIGNED NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_training_sport      FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_training_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_training_day CHECK (day_of_week IN
        ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
    CONSTRAINT chk_training_time CHECK (end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_training_sport_day ON training_schedules (sport_id, day_of_week);

-- ============================================================
-- 24. faqs
-- ============================================================
CREATE TABLE faqs (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    question      VARCHAR(255)    NOT NULL,
    answer        TEXT            NOT NULL,
    display_order INT             NOT NULL DEFAULT 0,
    is_active     TINYINT(1)      NOT NULL DEFAULT 1,
    created_by    BIGINT UNSIGNED NULL,
    updated_by    BIGINT UNSIGNED NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_faqs_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_faqs_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_faqs_active_order ON faqs (is_active, display_order);

-- ============================================================
-- 25. social_links  (single source of truth for social URLs)
-- ============================================================
CREATE TABLE social_links (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    platform      VARCHAR(50)     NOT NULL,               -- FACEBOOK|INSTAGRAM|LINKEDIN|YOUTUBE|TWITTER|...
    url           VARCHAR(255)    NOT NULL,
    icon_class    VARCHAR(60)     NULL,                   -- e.g. bi-facebook
    display_order INT             NOT NULL DEFAULT 0,
    is_active     TINYINT(1)      NOT NULL DEFAULT 1,
    created_by    BIGINT UNSIGNED NULL,
    updated_by    BIGINT UNSIGNED NULL,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_social_links_platform UNIQUE (platform),
    CONSTRAINT fk_social_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_social_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_social_active_order ON social_links (is_active, display_order);
```

---

## 6. Seed Data

```sql
-- Default sports
INSERT INTO sports (name, slug, display_order) VALUES
  ('Football','football',1), ('Cricket','cricket',2), ('Basketball','basketball',3),
  ('Volleyball','volleyball',4), ('Badminton','badminton',5),
  ('Table Tennis','table-tennis',6), ('Chess','chess',7);

-- Singleton config rows (id = 1)
INSERT INTO club_settings (id, university_name, club_name)
  VALUES (1, 'Southeast University', 'Games & Sports Club');
INSERT INTO system_settings (id, site_title, theme_color, footer_copyright, is_maintenance_mode)
  VALUES (1, 'SEU Games & Sports Club', '#0F766E',
          '© Southeast University Games & Sports Club', 0);

-- Default social links
INSERT INTO social_links (platform, url, icon_class, display_order) VALUES
  ('FACEBOOK','https://facebook.com/','bi-facebook',1),
  ('INSTAGRAM','https://instagram.com/','bi-instagram',2),
  ('LINKEDIN','https://linkedin.com/','bi-linkedin',3),
  ('YOUTUBE','https://youtube.com/','bi-youtube',4);

-- Admin created manually (BCrypt hash):
-- INSERT INTO users (email, password_hash, role, is_email_verified, is_enabled)
-- VALUES ('admin@seu.edu.bd', '$2a$10$....', 'ROLE_ADMIN', 1, 1);
```

---

## 7. Constraints Summary

**Unique:** `users.email`, `student_profiles.user_id`, `student_profiles.student_id`,
`sports.name/slug`, `events.slug`, `news.slug`, `gallery_categories.name/slug`,
`social_links.platform`, `(membership_period_id, student_user_id)`,
`(broadcast_id, recipient_user_id)`.

**Check:** role, gender, all `status` columns, `committee_type`, `notice_type`,
`target_type`, `purpose`, `media_type`, `day_of_week`, `achievement_year` /
`established_year` ranges, `closing_date >= opening_date`, `end_time > start_time`,
singleton `id = 1` on both settings tables.

**Cascade:** `CASCADE` for owned children (student profile, applications,
gallery images, broadcast recipients, training schedules); `RESTRICT` on
`gallery_images.media_id` (don't orphan an album image); `SET NULL` everywhere
else (audit `created_by`/`updated_by`, all `*_media_id`, authored-by/reviewer refs)
so content/history survives user or media deletion.

---

## 8. Index Summary (beyond PK / UQ / auto FK indexes)

| Table | Indexes |
|-------|---------|
| users | `role` |
| student_profiles | `department`, `batch` |
| membership_periods | `status`, `(opening_date, closing_date)` |
| membership_applications | `student_user_id`, `status` |
| committee_members | `(committee_type, display_order)` |
| events | `event_date`, `status` |
| news | `publish_date`, `status` |
| gallery_images | `(category_id, display_order)` |
| achievements | `achievement_year` |
| notices | `notice_type`, `publish_date` |
| contact_messages | `is_read` |
| email_broadcasts | `sent_at` |
| email_broadcast_recipients | `broadcast_id` |
| otp_tokens | `(email, purpose)`, `expires_at` |
| activity_logs | `created_by`, `created_at`, `(entity_type, entity_id)` |
| media_assets | `media_type` |
| hero_sliders / sponsors / faqs / social_links | `(is_active, display_order)` |
| training_schedules | `(sport_id, day_of_week)` |

> InnoDB automatically creates an index for every FK column, so `created_by`,
> `updated_by`, and all `*_media_id` columns are indexed without explicit `CREATE INDEX`.

---

## 9. Audit Strategy (created_by / updated_by / created_at / updated_at)

- Implemented once via a `@MappedSuperclass` **`Auditable`** base class that all
  business entities extend; `created_at`/`updated_at` use Hibernate
  `@CreationTimestamp`/`@UpdateTimestamp`, and `created_by`/`updated_by` are
  populated by Spring Data **`AuditorAware`** from the authenticated principal.
- All `*_by` columns are `BIGINT UNSIGNED NULL` → `users(id)` `ON DELETE SET NULL`,
  so deleting a user never cascades-delete content; it just nulls the stamp.
- **All entities extend `Auditable`** (per requirement). The former log tables
  (`otp_tokens`, `email_broadcast_recipients`, `activity_logs`) carry the full
  audit block too. `activity_logs` uses `created_by` as the acting admin (the
  old `actor_user_id` is removed). `membership_applications` additionally keeps
  `student_user_id` (applicant) + `reviewed_by` (reviewer) as domain actors.

---

## 10. Scalability & Optimization (designed for 10,000+ students)

> 10k students with ~25 related content tables is comfortably within MySQL/InnoDB
> single-node capacity. The design choices below keep reads fast and writes lean.

1. **Compact, sequential PKs** (`BIGINT UNSIGNED`) → high index density, no UUID
   page-split fragmentation; FK joins stay cache-friendly.
2. **Targeted indexes** on every column used in `WHERE`, `ORDER BY`, and FK joins
   (status, dates, `(is_active, display_order)` composites for landing-page lists).
3. **Pagination everywhere** — admin student/list screens use
   `Pageable`/`LIMIT … OFFSET` (or keyset pagination for very large sets); never
   load all 10k students at once.
4. **Lazy fetching + DTO projections** — all associations `FetchType.LAZY`;
   read screens use JPQL/`@Query` projections or `@EntityGraph` to prevent N+1.
5. **Centralized media** (`media_assets`) avoids wide rows and duplicate metadata;
   binaries stay on disk/object storage, the DB stores only paths + metadata.
6. **Connection pooling** (HikariCP, sized per profile) handles concurrent load.
7. **Write-light hot paths** — OTP/login/broadcast operations touch small,
   well-indexed tables; broadcast fan-out is asynchronous (`@Async`) and logged
   per recipient.
8. **Append-only logs** (`activity_logs`, `email_broadcast_recipients`) are
   indexed on `created_at` and are natural candidates for **time-based
   partitioning / periodic archival** if they grow large — no schema change needed.
9. **utf8mb4** throughout for full Unicode (Bangla/emoji) safety.
10. **`open-in-view=false`** (already set) keeps DB sessions short, freeing
    connections quickly under load.

---

## 11. JPA Relationship Mapping Plan (no code yet — annotations only)

> Strategy: `FetchType.LAZY` everywhere; owning side holds `@JoinColumn`;
> enums `@Enumerated(STRING)`; audit via `Auditable` `@MappedSuperclass` + JPA
> Auditing (`AuditorAware`).

| Relationship | Owning side (FK) | Inverse side |
|--------------|------------------|--------------|
| User ↔ StudentProfile (1:1) | `StudentProfile @OneToOne @JoinColumn(user_id, unique)` | `User @OneToOne(mappedBy, cascade=ALL, orphanRemoval)` |
| MembershipPeriod → MembershipApplication (1:N) | `@ManyToOne @JoinColumn(membership_period_id)` | `@OneToMany(mappedBy="period")` |
| User → MembershipApplication (student / reviewer) | `@ManyToOne @JoinColumn(student_user_id)` / `(reviewed_by)` | not mapped |
| Sport → Event / Achievement (1:N opt) | `@ManyToOne @JoinColumn(sport_id)` | optional `@OneToMany(mappedBy="sport")` |
| Sport → TrainingSchedule (1:N) | `@ManyToOne @JoinColumn(sport_id)` | `@OneToMany(mappedBy="sport")` |
| GalleryCategory → GalleryImage (1:N) | `@ManyToOne @JoinColumn(category_id)` | `@OneToMany(mappedBy="category", cascade=ALL, orphanRemoval)` |
| EmailBroadcast → EmailBroadcastRecipient (1:N) | `@ManyToOne @JoinColumn(broadcast_id)` | `@OneToMany(mappedBy="broadcast", cascade=ALL, orphanRemoval)` |
| **MediaAsset → any table** (1:N opt) | `@ManyToOne @JoinColumn(*_media_id)` on each owner | not mapped (MediaAsset stays a lean shared root) |
| User → (audit) every Auditable entity | `@ManyToOne @JoinColumn(created_by)` / `(updated_by)` (in base class) | not mapped |

**Singletons:** `ClubSettings`, `SystemSettings` — single-row entities loaded by `id = 1`.

**Standalone entities:** `CommitteeMember`, `ContactMessage`, `OtpToken`,
`HeroSlider`, `Sponsor`, `Faq`, `SocialLink` (each may reference `MediaAsset`/audit only).

**Enums (mapped `STRING`):** `Role`, `Gender`, `MembershipStatus`,
`ApplicationStatus`, `CommitteeType`, `EventStatus`, `PublishStatus`, `NoticeType`,
`BroadcastTarget`, `BroadcastStatus`, `DeliveryStatus`, `OtpPurpose`, `MediaType`,
`SocialPlatform`, `DayOfWeek`.
