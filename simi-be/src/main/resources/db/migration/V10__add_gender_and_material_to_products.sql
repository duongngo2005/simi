ALTER TABLE products
    ADD COLUMN gender VARCHAR(20) NOT NULL DEFAULT 'UNISEX',
ADD COLUMN material VARCHAR(100) NULL;

CREATE INDEX idx_products_gender ON products(gender);
CREATE INDEX idx_products_material ON products(material);