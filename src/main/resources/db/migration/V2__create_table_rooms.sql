-- ==============================================================
-- V2 - Create table: rooms
-- ==============================================================
-- room_type uses VARCHAR(20) to match JPA @Enumerated(EnumType.STRING).
-- FK to projects with ON DELETE CASCADE mirrors orphanRemoval = true in JPA.

CREATE TABLE IF NOT EXISTS rooms
(
    id           BIGSERIAL    NOT NULL,
    name         VARCHAR(150) NOT NULL,
    room_type    VARCHAR(20),
    width        NUMERIC(10, 2),
    length       NUMERIC(10, 2),
    height       NUMERIC(10, 2),
    door_count   INTEGER,
    window_count INTEGER,
    observations TEXT,
    project_id   BIGINT       NOT NULL,

    CONSTRAINT pk_rooms PRIMARY KEY (id),
    CONSTRAINT chk_rooms_room_type
        CHECK (room_type IN (
            'BEDROOM', 'KITCHEN', 'LIVING_ROOM', 'BATHROOM',
            'DINING_ROOM', 'GARAGE', 'OFFICE', 'PATIO', 'OTHER'
        )),
    CONSTRAINT fk_rooms_project
        FOREIGN KEY (project_id)
            REFERENCES projects (id)
            ON DELETE CASCADE
);

-- Indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_rooms_project_id ON rooms (project_id);
CREATE INDEX IF NOT EXISTS idx_rooms_room_type  ON rooms (room_type);
