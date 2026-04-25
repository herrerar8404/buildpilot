-- ==============================================================
-- BuildPilot - Manual DB fix: repair Flyway V3
-- Run this script ONCE directly against the buildpilot database.
-- After running it, start the Spring Boot app normally.
-- ==============================================================

-- Step 1: Show current Flyway state
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- Step 2: Delete the failed V3 entry so Flyway can re-run it
DELETE FROM flyway_schema_history
WHERE version = '3' AND success = false;

-- Step 3: Create the materials table (idempotent)
CREATE TABLE IF NOT EXISTS materials
(
    id            BIGSERIAL      NOT NULL,
    name          VARCHAR(150)   NOT NULL,
    material_type VARCHAR(30)    NOT NULL,
    unit_measure  VARCHAR(20)    NOT NULL,
    unit_price    NUMERIC(12, 2) NOT NULL,
    description   TEXT,
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP      NOT NULL,
    updated_at    TIMESTAMP,

    CONSTRAINT pk_materials PRIMARY KEY (id),
    CONSTRAINT uq_materials_name UNIQUE (name),
    CONSTRAINT chk_materials_unit_price
        CHECK (unit_price > 0),
    CONSTRAINT chk_materials_material_type
        CHECK (material_type IN (
            'CEMENT', 'BRICK', 'STEEL', 'PAINT', 'FLOORING',
            'SAND', 'GRAVEL', 'WOOD', 'GLASS', 'OTHER'
            )),
    CONSTRAINT chk_materials_unit_measure
        CHECK (unit_measure IN (
            'PIECE', 'BAG', 'KG', 'TON', 'M2', 'M3', 'LITER', 'UNIT'
            ))
);

CREATE INDEX IF NOT EXISTS idx_materials_material_type ON materials (material_type);
CREATE INDEX IF NOT EXISTS idx_materials_is_active     ON materials (is_active);

-- Step 4: Show final Flyway state
-- Flyway will insert its own correct V3 row on next app startup.
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
