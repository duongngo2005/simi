ALTER TABLE payments
    ADD COLUMN refunded_at DATETIME NULL,
    ADD COLUMN transaction_id VARCHAR(255) NULL;
