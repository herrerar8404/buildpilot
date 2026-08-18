-- ==============================================================
-- V13 - Add template_id relation to construction_elements
-- ==============================================================
-- Note: version 9 is already used in this project history,
-- so this migration keeps the requested intent with the next version.

ALTER TABLE construction_elements
    ADD COLUMN IF NOT EXISTS template_id BIGINT;

-- Backfill from legacy column introduced earlier in local environments.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'construction_elements'
          AND column_name = 'construction_template_id'
    ) THEN
        UPDATE construction_elements
        SET template_id = construction_template_id
        WHERE template_id IS NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'construction_elements'
          AND constraint_name = 'fk_construction_elements_template_id'
    ) THEN
        ALTER TABLE construction_elements
            ADD CONSTRAINT fk_construction_elements_template_id
                FOREIGN KEY (template_id)
                REFERENCES construction_templates (id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_construction_elements_template_id
    ON construction_elements (template_id);

