-- ==============================================================
-- V14 - Enforce unique material per construction element
-- ==============================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'element_materials'
          AND constraint_name = 'uq_element_materials_element_material'
    ) THEN
        ALTER TABLE element_materials
            ADD CONSTRAINT uq_element_materials_element_material
                UNIQUE (construction_element_id, material_id);
    END IF;
END $$;

