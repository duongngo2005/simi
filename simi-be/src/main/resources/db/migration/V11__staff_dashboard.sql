ALTER TABLE orders
    ADD COLUMN completed_at DATETIME NULL;

CREATE TABLE staff_notifications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    notification_type VARCHAR(40) NOT NULL,
    read_at DATETIME NULL,
    created_date DATETIME NOT NULL,
    updated_date DATETIME NOT NULL,

    CONSTRAINT fk_staff_notifications_recipient
     FOREIGN KEY (recipient_id) REFERENCES users(id),

    CONSTRAINT fk_staff_notifications_order
     FOREIGN KEY (order_id) REFERENCES orders(id),

    CONSTRAINT uk_staff_notifications_recipient_order_type
     UNIQUE (recipient_id, order_id, notification_type)
);