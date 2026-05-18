-- V7: Create quotations table
-- One quotation per project (OneToOne relationship).

CREATE TABLE quotations (
    id            BIGSERIAL       NOT NULL,
    project_id    BIGINT          NOT NULL,

    material_cost NUMERIC(14, 2)  NOT NULL,
    labor_cost    NUMERIC(14, 2)  NOT NULL,
    indirect_cost NUMERIC(14, 2)  NOT NULL,
    profit_margin NUMERIC(7,  2)  NOT NULL,
    subtotal      NUMERIC(14, 2)  NOT NULL,
    total_cost    NUMERIC(14, 2)  NOT NULL,

    notes         TEXT,

    created_at    TIMESTAMP       NOT NULL,
    updated_at    TIMESTAMP,

    CONSTRAINT pk_quotations             PRIMARY KEY (id),
    CONSTRAINT uq_quotations_project_id  UNIQUE      (project_id),
    CONSTRAINT fk_quotations_project_id  FOREIGN KEY (project_id)
        REFERENCES projects (id)
        ON DELETE CASCADE
);

-- Index for FK lookups
CREATE INDEX idx_quotations_project_id ON quotations (project_id);

