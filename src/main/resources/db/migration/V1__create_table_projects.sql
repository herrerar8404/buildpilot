-- ==============================================================
-- V1 - Create table: projects
-- ==============================================================
-- status uses VARCHAR(20) to match JPA @Enumerated(EnumType.STRING).
-- A CHECK constraint enforces valid values at the database level.

CREATE TABLE IF NOT EXISTS projects
(
    id                BIGSERIAL    NOT NULL,
    project_name      VARCHAR(150) NOT NULL,
    client_name       VARCHAR(150) NOT NULL,
    construction_type VARCHAR(100),
    land_width        NUMERIC(10, 2),
    land_length       NUMERIC(10, 2),
    address           VARCHAR(255),
    description       TEXT,
    creation_date     DATE,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING',

    CONSTRAINT pk_projects PRIMARY KEY (id),
    CONSTRAINT chk_projects_status
        CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

-- Indexes for common query patterns (use IF NOT EXISTS to make idempotent)
CREATE INDEX IF NOT EXISTS idx_projects_status      ON projects (status);
CREATE INDEX IF NOT EXISTS idx_projects_client_name ON projects (client_name);
