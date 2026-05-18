-- ==============================================================
-- V7 - Add floor-plan position coordinates to rooms
-- ==============================================================
-- position_x / position_y are nullable to keep backward compatibility
-- with existing rooms created before floor-plan support.

ALTER TABLE rooms
    ADD COLUMN IF NOT EXISTS position_x NUMERIC(10, 2),
    ADD COLUMN IF NOT EXISTS position_y NUMERIC(10, 2);

ALTER TABLE rooms
    ADD CONSTRAINT chk_rooms_position_x_non_negative
        CHECK (position_x IS NULL OR position_x >= 0),
    ADD CONSTRAINT chk_rooms_position_y_non_negative
        CHECK (position_y IS NULL OR position_y >= 0);

