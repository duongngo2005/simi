ALTER TABLE price_schedules
DROP COLUMN sequence_no;
-- thêm check trùng ngày giám giá trong db
ALTER TABLE price_schedules
    ADD CONSTRAINT uq_price_schedules_item_days
        UNIQUE (consignment_item_id, effective_after_days);