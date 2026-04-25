-- ==============================================================
-- V5 - Create table: element_materials  (idempotent)
-- ==============================================================
-- Junction table between construction_elements and materials.
-- Stores real calculation data (quantities, waste, cost).
-- FKs with ON DELETE CASCADE mirror orphanRemoval = true in JPA.

CREATE TABLE IF NOT EXISTS element_materials
(
    id                       BIGSERIAL      NOT NULL,
    required_quantity        NUMERIC(14, 4) NOT NULL,
    performance_per_m2       NUMERIC(14, 4),
    waste_percentage         NUMERIC(7, 2),
    final_quantity           NUMERIC(14, 4),
    calculated_cost          NUMERIC(16, 2),
    notes                    TEXT,
    created_at               TIMESTAMP      NOT NULL,
    updated_at               TIMESTAMP,
    construction_element_id  BIGINT         NOT NULL,
    material_id              BIGINT         NOT NULL,

    CONSTRAINT pk_element_materials PRIMARY KEY (id),
    CONSTRAINT fk_element_materials_element
        FOREIGN KEY (construction_element_id)
            REFERENCES construction_elements (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_element_materials_material
        FOREIGN KEY (material_id)
            REFERENCES materials (id),
    CONSTRAINT chk_required_quantity
        CHECK (required_quantity > 0),
    CONSTRAINT chk_waste_percentage
        CHECK (waste_percentage IS NULL OR waste_percentage >= 0),
    CONSTRAINT chk_final_quantity
        CHECK (final_quantity IS NULL OR final_quantity > 0),
    CONSTRAINT chk_calculated_cost
        CHECK (calculated_cost IS NULL OR calculated_cost >= 0)
);

CREATE INDEX IF NOT EXISTS idx_element_materials_element_id  ON element_materials (construction_element_id);
CREATE INDEX IF NOT EXISTS idx_element_materials_material_id ON element_materials (material_id);
