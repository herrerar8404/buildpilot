-- ==============================================================
-- V11 - Seed starter construction templates
-- ==============================================================

INSERT INTO construction_templates (name, description, category, unit_type, is_active, created_at, updated_at)
VALUES
    ('Block Wall', 'Reusable template for hollow block masonry walls', 'MASONRY', 'M2', TRUE, NOW(), NOW()),
    ('Concrete Slab', 'Template for reinforced concrete slab sections', 'STRUCTURAL', 'M2', TRUE, NOW(), NOW()),
    ('Ceramic Floor', 'Template for ceramic floor finish layers', 'FINISHES', 'M2', TRUE, NOW(), NOW()),
    ('Interior Paint', 'Template for interior acrylic painting works', 'PAINTING', 'M2', TRUE, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

