CREATE TABLE account
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 100000 INCREMENT BY 5) PRIMARY KEY,
    name        VARCHAR(30)  NOT NULL,
    phone       VARCHAR(20)  NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    wb_api_key  VARCHAR(500) NOT NULL UNIQUE,
    telegram_id BIGINT       NOT NULL UNIQUE
);

CREATE TABLE fulfillment
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 10000000 INCREMENT BY 5) PRIMARY KEY,
    account_id       BIGINT                   NOT NULL,
    date             TIMESTAMP WITH TIME ZONE NOT NULL,
    title            VARCHAR(255)             NOT NULL,
    article          VARCHAR(100)             NOT NULL,
    barcode          VARCHAR(100)             NOT NULL,
    size             VARCHAR(255),
    color            VARCHAR(255),
    photo_url        VARCHAR(1024),
    quantity         INTEGER                  NOT NULL,
    task_description TEXT,
    CONSTRAINT fk_fulfillment_account
        FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);

CREATE TABLE shipment
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 10000000 INCREMENT BY 5) PRIMARY KEY,
    fulfillment_id BIGINT                   NOT NULL,
    date           TIMESTAMP WITH TIME ZONE NOT NULL,
    quantity       INTEGER                  NOT NULL,
    warehouse_id   BIGINT                   NOT NULL,
    warehouse_name VARCHAR(255)             NOT NULL,
    status         VARCHAR(50),
    CONSTRAINT fk_shipment_fulfillment
        FOREIGN KEY (fulfillment_id) REFERENCES fulfillment (id) ON DELETE CASCADE
);

CREATE TABLE product
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 10000000 INCREMENT BY 5) PRIMARY KEY,
    account_id BIGINT                   NOT NULL,
    title      VARCHAR(255)             NOT NULL,
    barcode    VARCHAR(100)             NOT NULL,
    article    VARCHAR(100)             NOT NULL,
    size       VARCHAR(255)             NOT NULL,
    color      VARCHAR(255),
    photo_url  VARCHAR(1024),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_product_account
        FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);

CREATE INDEX idx_product_account_id ON product(account_id);