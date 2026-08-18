-- ==============================================================
-- V9 - Create table: construction_templates  (idempotent)
-- ==============================================================

CREATE TABLE IF NOT EXISTS construction_templates
(
    id          BIGSERIAL     NOT NULL,
    name        VARCHAR(150)  NOT NULL,
    description TEXT,
    category    VARCHAR(30)   NOT NULL,
    unit_type   VARCHAR(20)   NOT NULL,
    is_active   BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP     NOT NULL,
    updated_at  TIMESTAMP,

    CONSTRAINT pk_construction_templates PRIMARY KEY (id),
    CONSTRAINT uq_construction_templates_name UNIQUE (name),
    CONSTRAINT chk_construction_templates_category
        CHECK (category IN ('MASONRY', 'STRUCTURAL', 'FINISHES', 'PAINTING', 'OTHER')),
    CONSTRAINT chk_construction_templates_unit_type
        CHECK (unit_type IN ('PIECE', 'BAG', 'KG', 'TON', 'M2', 'M3', 'LITER', 'UNIT'))
);

CREATE INDEX IF NOT EXISTS idx_construction_templates_category ON construction_templates (category);
CREATE INDEX IF NOT EXISTS idx_construction_templates_active   ON construction_templates (is_active);

