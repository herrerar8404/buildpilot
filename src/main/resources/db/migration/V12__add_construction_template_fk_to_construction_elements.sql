-- ==============================================================
-- V12 - Prepare construction_elements for template linkage
-- ==============================================================

ALTER TABLE construction_elements
    ADD COLUMN IF NOT EXISTS construction_template_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_construction_elements_template'
          AND table_name = 'construction_elements'
    ) THEN
        ALTER TABLE construction_elements
            ADD CONSTRAINT fk_construction_elements_template
                FOREIGN KEY (construction_template_id)
                REFERENCES construction_templates (id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_elements_template_id ON construction_elements (construction_template_id);

