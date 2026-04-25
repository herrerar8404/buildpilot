-- ==============================================================
-- BuildPilot - Full DB repair (V3 · V4 · V5)
-- Run ONCE via pgAdmin or double-click run_fix_db.bat
-- After this, start the Spring Boot app normally.
-- ==============================================================

-- ── 1. Show current state ──────────────────────────────────────
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- ── 2. Remove ALL failed AND outdated Flyway entries for V3-V5 ─
-- (covers both success=false rows AND stale checksum rows)
DELETE FROM flyway_schema_history
WHERE version IN ('3', '4', '5');

-- ── 3. Create materials (V3) — idempotent ─────────────────────
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
    CONSTRAINT chk_materials_unit_price CHECK (unit_price > 0),
    CONSTRAINT chk_materials_material_type
        CHECK (material_type IN ('CEMENT','BRICK','STEEL','PAINT','FLOORING','SAND','GRAVEL','WOOD','GLASS','OTHER')),
    CONSTRAINT chk_materials_unit_measure
        CHECK (unit_measure IN ('PIECE','BAG','KG','TON','M2','M3','LITER','UNIT'))
);
CREATE INDEX IF NOT EXISTS idx_materials_material_type ON materials (material_type);
CREATE INDEX IF NOT EXISTS idx_materials_is_active     ON materials (is_active);

-- ── 4. Create construction_elements (V4) — idempotent ──────────
CREATE TABLE IF NOT EXISTS construction_elements
(
    id              BIGSERIAL     NOT NULL,
    name            VARCHAR(150)  NOT NULL,
    element_type    VARCHAR(30)   NOT NULL,
    quantity        NUMERIC(14,4),
    unit_measure    VARCHAR(20),
    calculated_area NUMERIC(14,4),
    description     TEXT,
    created_at      TIMESTAMP     NOT NULL,
    updated_at      TIMESTAMP,
    room_id         BIGINT        NOT NULL,
    CONSTRAINT pk_construction_elements PRIMARY KEY (id),
    CONSTRAINT fk_elements_room FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE,
    CONSTRAINT chk_elements_element_type
        CHECK (element_type IN ('WALL','FLOOR','ROOF','SLAB','COLUMN','DOOR','WINDOW','STAIRS','CEILING','OTHER')),
    CONSTRAINT chk_elements_unit_measure
        CHECK (unit_measure IS NULL OR unit_measure IN ('PIECE','BAG','KG','TON','M2','M3','LITER','UNIT')),
    CONSTRAINT chk_elements_quantity        CHECK (quantity IS NULL OR quantity > 0),
    CONSTRAINT chk_elements_calculated_area CHECK (calculated_area IS NULL OR calculated_area > 0)
);
CREATE INDEX IF NOT EXISTS idx_elements_room_id      ON construction_elements (room_id);
CREATE INDEX IF NOT EXISTS idx_elements_element_type ON construction_elements (element_type);

-- ── 5. Create element_materials (V5) — idempotent ─────────────
CREATE TABLE IF NOT EXISTS element_materials
(
    id                      BIGSERIAL      NOT NULL,
    required_quantity       NUMERIC(14, 4) NOT NULL,
    performance_per_m2      NUMERIC(14, 4),
    waste_percentage        NUMERIC(7, 2),
    final_quantity          NUMERIC(14, 4),
    calculated_cost         NUMERIC(16, 2),
    notes                   TEXT,
    created_at              TIMESTAMP      NOT NULL,
    updated_at              TIMESTAMP,
    construction_element_id BIGINT         NOT NULL,
    material_id             BIGINT         NOT NULL,
    CONSTRAINT pk_element_materials PRIMARY KEY (id),
    CONSTRAINT fk_element_materials_element
        FOREIGN KEY (construction_element_id) REFERENCES construction_elements (id) ON DELETE CASCADE,
    CONSTRAINT fk_element_materials_material
        FOREIGN KEY (material_id) REFERENCES materials (id),
    CONSTRAINT chk_required_quantity   CHECK (required_quantity > 0),
    CONSTRAINT chk_waste_percentage    CHECK (waste_percentage IS NULL OR waste_percentage >= 0),
    CONSTRAINT chk_final_quantity      CHECK (final_quantity IS NULL OR final_quantity > 0),
    CONSTRAINT chk_calculated_cost     CHECK (calculated_cost IS NULL OR calculated_cost >= 0)
);
CREATE INDEX IF NOT EXISTS idx_element_materials_element_id  ON element_materials (construction_element_id);
CREATE INDEX IF NOT EXISTS idx_element_materials_material_id ON element_materials (material_id);

-- ── 6. Confirm result ─────────────────────────────────────────
-- Flyway will re-insert correct checksum rows for V3/V4/V5
-- on next startup (repair-on-migrate=true takes care of it).
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
