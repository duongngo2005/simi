create table carts (
    id           bigint auto_increment primary key,
    created_date datetime not null,
    updated_date datetime not null,
    user_id      bigint   not null,
    constraint uq_carts_user    unique (user_id),
    constraint fk_carts_user    foreign key (user_id) references users(id)
) engine=innodb;

create table cart_items (
    id           bigint auto_increment primary key,
    created_date datetime not null,
    updated_date datetime not null,
    cart_id      bigint   not null,
    product_id   bigint   not null,
    constraint uq_cart_items_product unique (product_id),
    constraint fk_cart_items_cart    foreign key (cart_id)    references carts(id),
    constraint fk_cart_items_product foreign key (product_id) references products(id)
) engine=innodb;
