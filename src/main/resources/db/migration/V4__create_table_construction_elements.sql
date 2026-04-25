-- ==============================================================
-- V4 - Create table: construction_elements  (idempotent)
-- ==============================================================
-- element_type and unit_measure stored as VARCHAR to match
-- JPA @Enumerated(EnumType.STRING).
-- FK to rooms with ON DELETE CASCADE mirrors orphanRemoval = true.

CREATE TABLE IF NOT EXISTS construction_elements
(
    id               BIGSERIAL     NOT NULL,
    name             VARCHAR(150)  NOT NULL,
    element_type     VARCHAR(30)   NOT NULL,
    quantity         NUMERIC(14, 4),
    unit_measure     VARCHAR(20),
    calculated_area  NUMERIC(14, 4),
    description      TEXT,
    created_at       TIMESTAMP     NOT NULL,
    updated_at       TIMESTAMP,
    room_id          BIGINT        NOT NULL,

    CONSTRAINT pk_construction_elements PRIMARY KEY (id),
    CONSTRAINT fk_elements_room
        FOREIGN KEY (room_id)
            REFERENCES rooms (id)
            ON DELETE CASCADE,
    CONSTRAINT chk_elements_element_type
        CHECK (element_type IN (
            'WALL', 'FLOOR', 'ROOF', 'SLAB', 'COLUMN',
            'DOOR', 'WINDOW', 'STAIRS', 'CEILING', 'OTHER'
        )),
    CONSTRAINT chk_elements_unit_measure
        CHECK (unit_measure IS NULL OR unit_measure IN (
            'PIECE', 'BAG', 'KG', 'TON', 'M2', 'M3', 'LITER', 'UNIT'
        )),
    CONSTRAINT chk_elements_quantity
        CHECK (quantity IS NULL OR quantity > 0),
    CONSTRAINT chk_elements_calculated_area
        CHECK (calculated_area IS NULL OR calculated_area > 0)
);

CREATE INDEX IF NOT EXISTS idx_elements_room_id      ON construction_elements (room_id);
CREATE INDEX IF NOT EXISTS idx_elements_element_type ON construction_elements (element_type);
