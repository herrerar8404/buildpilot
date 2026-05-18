-- ==============================================================
-- V8 - Evolve rooms for shape support (rectangle/open/irregular)
-- ==============================================================
-- Keeps backward compatibility with existing rectangular rooms.

ALTER TABLE rooms
    ADD COLUMN IF NOT EXISTS shape_type VARCHAR(20) NOT NULL DEFAULT 'RECTANGLE',
    ADD COLUMN IF NOT EXISTS wall_count INTEGER NOT NULL DEFAULT 4;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_rooms_shape_type'
    ) THEN
        ALTER TABLE rooms
            ADD CONSTRAINT chk_rooms_shape_type
                CHECK (shape_type IN ('RECTANGLE', 'OPEN_AREA', 'IRREGULAR'));
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_rooms_wall_count_by_shape'
    ) THEN
        ALTER TABLE rooms
            ADD CONSTRAINT chk_rooms_wall_count_by_shape
                CHECK (
                    (shape_type = 'OPEN_AREA' AND wall_count >= 2)
                    OR (shape_type IN ('RECTANGLE', 'IRREGULAR') AND wall_count >= 3)
                );
    END IF;
END $$;

