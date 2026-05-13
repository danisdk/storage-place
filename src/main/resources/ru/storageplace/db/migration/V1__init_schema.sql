CREATE TABLE warehouse
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    address     VARCHAR(255),
    description TEXT
);

CREATE TABLE product
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(150)   NOT NULL,
    article   VARCHAR(80)    NOT NULL UNIQUE,
    length_cm DECIMAL(14, 3) NOT NULL CHECK (length_cm > 0),
    width_cm  DECIMAL(14, 3) NOT NULL CHECK (width_cm > 0),
    height_cm DECIMAL(14, 3) NOT NULL CHECK (height_cm > 0),
    weight_kg DECIMAL(14, 3) NOT NULL CHECK (weight_kg > 0)
);

CREATE TABLE storage_place_type
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE storage_place
(
    id            BIGSERIAL PRIMARY KEY,
    warehouse_id  BIGINT         NOT NULL REFERENCES warehouse (id) ON DELETE CASCADE,
    type_id       BIGINT         REFERENCES storage_place_type (id) ON DELETE SET NULL,
    number        VARCHAR(50)    NOT NULL,
    length_cm     DECIMAL(14, 3) NOT NULL CHECK (length_cm > 0),
    width_cm      DECIMAL(14, 3) NOT NULL CHECK (width_cm > 0),
    height_cm     DECIMAL(14, 3) NOT NULL CHECK (height_cm > 0),
    max_weight_kg DECIMAL(14, 3) NOT NULL CHECK (max_weight_kg > 0),

    CONSTRAINT uq_storage_place_number
        UNIQUE NULLS NOT DISTINCT (warehouse_id, type_id, number)
);

CREATE TABLE storage_place_state
(
    id                 BIGSERIAL PRIMARY KEY,
    storage_place_id   BIGINT                   NOT NULL REFERENCES storage_place (id) ON DELETE CASCADE UNIQUE,
    occupied_volume    DECIMAL(18, 3)           NOT NULL DEFAULT 0 CHECK (occupied_volume >= 0),
    occupied_weight_kg DECIMAL(18, 3)           NOT NULL DEFAULT 0 CHECK (occupied_weight_kg >= 0),
    status             VARCHAR(30)              NOT NULL DEFAULT 'FREE',
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_storage_place_status
        CHECK (status IN ('FREE', 'PARTIALLY_OCCUPIED', 'OCCUPIED'))
);

CREATE TABLE storage_balance
(
    id               BIGSERIAL PRIMARY KEY,
    storage_place_id BIGINT         NOT NULL REFERENCES storage_place (id) ON DELETE CASCADE,
    product_id       BIGINT         NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    quantity         INTEGER        NOT NULL CHECK (quantity >= 0),
    total_volume     DECIMAL(18, 3) NOT NULL DEFAULT 0 CHECK (total_volume >= 0),
    total_weight_kg  DECIMAL(18, 3) NOT NULL DEFAULT 0 CHECK (total_weight_kg >= 0),

    CONSTRAINT uq_storage_balance_storage_place_product
        UNIQUE (storage_place_id, product_id)
);

CREATE TABLE storage_operation
(
    id                BIGSERIAL PRIMARY KEY,
    operation_type    VARCHAR(30)              NOT NULL,
    product_id        BIGINT                   NOT NULL REFERENCES product (id) ON DELETE RESTRICT,
    source_place_id   BIGINT REFERENCES storage_place (id) ON DELETE RESTRICT,
    target_place_id   BIGINT REFERENCES storage_place (id) ON DELETE RESTRICT,
    quantity          INTEGER                  NOT NULL CHECK (quantity > 0),

    product_length_cm DECIMAL(14, 3)           NOT NULL,
    product_width_cm  DECIMAL(14, 3)           NOT NULL,
    product_height_cm DECIMAL(14, 3)           NOT NULL,
    product_weight_kg DECIMAL(14, 3)           NOT NULL,
    product_volume    DECIMAL(18, 3)           NOT NULL,
    total_volume      DECIMAL(18, 3)           NOT NULL,
    total_weight_kg   DECIMAL(18, 3)           NOT NULL,

    status            VARCHAR(30)              NOT NULL,
    result_message    TEXT,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at      TIMESTAMP WITH TIME ZONE,

    CONSTRAINT chk_storage_operation_type
        CHECK (operation_type IN ('INCOME', 'OUTCOME', 'TRANSFER')),

    CONSTRAINT chk_storage_operation_status
        CHECK (status IN ('CALCULATED', 'CONFIRMED', 'REJECTED'))
);

CREATE INDEX idx_storage_place_warehouse_id ON storage_place (warehouse_id);
CREATE INDEX idx_storage_place_type_id ON storage_place (type_id);
CREATE INDEX idx_storage_balance_storage_place_id ON storage_balance (storage_place_id);
CREATE INDEX idx_storage_balance_product_id ON storage_balance (product_id);
CREATE INDEX idx_storage_operation_product_id ON storage_operation (product_id);
CREATE INDEX idx_storage_operation_source_place_id ON storage_operation (source_place_id);
CREATE INDEX idx_storage_operation_target_place_id ON storage_operation (target_place_id);