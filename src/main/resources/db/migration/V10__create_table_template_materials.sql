-- ==============================================================
-- V10 - Create table: template_materials  (idempotent)
-- ==============================================================

CREATE TABLE IF NOT EXISTS template_materials
(
    id                        BIGSERIAL      NOT NULL,
    quantity_per_unit         NUMERIC(14, 4) NOT NULL,
    waste_percentage          NUMERIC(7, 2),
    notes                     TEXT,
    created_at                TIMESTAMP      NOT NULL,
    updated_at                TIMESTAMP,
    construction_template_id  BIGINT         NOT NULL,
    material_id               BIGINT         NOT NULL,

    CONSTRAINT pk_template_materials PRIMARY KEY (id),
    CONSTRAINT uq_template_materials_template_material UNIQUE (construction_template_id, material_id),
    CONSTRAINT fk_template_materials_template
        FOREIGN KEY (construction_template_id)
            REFERENCES construction_templates (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_template_materials_material
        FOREIGN KEY (material_id)
            REFERENCES materials (id),
    CONSTRAINT chk_template_materials_quantity_per_unit
        CHECK (quantity_per_unit > 0),
    CONSTRAINT chk_template_materials_waste_percentage
        CHECK (waste_percentage IS NULL OR waste_percentage >= 0)
);

CREATE INDEX IF NOT EXISTS idx_template_materials_template_id ON template_materials (construction_template_id);
CREATE INDEX IF NOT EXISTS idx_template_materials_material_id ON template_materials (material_id);

