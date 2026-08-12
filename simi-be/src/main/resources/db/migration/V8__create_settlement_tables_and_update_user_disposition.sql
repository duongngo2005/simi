-- 1. Tạo bảng phiếu quyết toán mới
CREATE TABLE IF NOT EXISTS settlements (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    consignment_id          BIGINT NOT NULL UNIQUE,
    processed_by_id         BIGINT NOT NULL,

    total_sold_amount       DECIMAL(12, 0),
    total_commission_amount DECIMAL(12, 0),
    net_amount              DECIMAL(12, 0),

    payment_method          VARCHAR(255) DEFAULT 'BANK_TRANSFER',
    bank_name               VARCHAR(255),
    account_number          VARCHAR(255),
    account_holder          VARCHAR(255),

    proof_image_url         VARCHAR(255) NOT NULL,

    settled_at              DATETIME(6),

    created_date            DATETIME(6) NOT NULL,
    updated_date            DATETIME(6) NOT NULL,

    CONSTRAINT fk_settlement_consignment FOREIGN KEY (consignment_id) REFERENCES consignments(id),
    CONSTRAINT fk_settlement_processed_by FOREIGN KEY (processed_by_id) REFERENCES users(id)
);

-- 2. Thêm khóa ngoại settlement_id vào consignment_items
ALTER TABLE consignment_items
    ADD COLUMN settlement_id BIGINT NULL;

-- 3. Thêm thông tin tài khoản ngân hàng vào users
ALTER TABLE users
    ADD COLUMN bank_name       VARCHAR(255) NULL,
    ADD COLUMN account_number  VARCHAR(255) NULL,
    ADD COLUMN account_holder  VARCHAR(255) NULL;
