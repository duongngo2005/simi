alter table cart_items drop foreign key fk_cart_items_product;

alter table cart_items drop index uq_cart_items_product;

alter table cart_items add constraint uq_cart_items_cart_product unique (cart_id, product_id);

alter table cart_items add constraint fk_cart_items_product foreign key (product_id) references products(id);
