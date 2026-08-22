ALTER TABLE payments
    ADD COLUMN payment_provider VARCHAR(30) NOT NULL DEFAULT 'NONE' AFTER payment_method,
    ADD COLUMN gateway_transaction_ref VARCHAR(100) NULL AFTER payment_status,
    ADD COLUMN gateway_transaction_id VARCHAR(255) NULL AFTER gateway_transaction_ref,
    ADD COLUMN gateway_bank_code VARCHAR(50) NULL AFTER gateway_transaction_id,
    ADD COLUMN gateway_response_code VARCHAR(50) NULL AFTER gateway_bank_code,
    ADD COLUMN gateway_created_at DATETIME NULL AFTER gateway_response_code,
    ADD COLUMN expires_at DATETIME NULL AFTER gateway_created_at;

UPDATE payments SET gateway_transaction_id = transaction_id WHERE transaction_id IS NOT NULL;
ALTER TABLE payments DROP COLUMN transaction_id;

CREATE UNIQUE INDEX uq_payments_gateway_txn_ref ON payments (gateway_transaction_ref);
CREATE INDEX idx_payments_order_status ON payments (order_id, payment_status);

UPDATE payments SET payment_method = 'ONLINE', payment_provider = 'VNPAY' WHERE payment_method = 'BANK_TRANSFER';

ALTER TABLE products
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE orders
    ADD COLUMN reservation_expires_at DATETIME NULL AFTER discount,
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

CREATE INDEX idx_orders_status_reservation ON orders (order_status, reservation_expires_at);

ALTER TABLE settlements
    MODIFY COLUMN payment_method VARCHAR(255) NOT NULL DEFAULT 'ONLINE';