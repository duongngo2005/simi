create table products (
    id                bigint auto_increment primary key,
    created_date      datetime     not null,
    updated_date      datetime     not null,
    name              varchar(255) not null,
    size              varchar(255),
    description       text,
    color             varchar(255),
    current_price     decimal(12, 0),
    product_condition varchar(50),
    product_status    varchar(50),
    category_id       bigint       not null,
    brand_id          bigint,
    constraint fk_products_category foreign key (category_id) references categories(id),
    constraint fk_products_brand    foreign key (brand_id)    references brands(id)
) engine=innodb;

create table product_images (
    id               bigint auto_increment primary key,
    created_date     datetime     not null,
    updated_date     datetime     not null,
    image_url        varchar(255) not null,
    image_public_id  varchar(255) not null,
    thumbnail        boolean      not null default false,
    product_id       bigint       not null,
    constraint fk_product_images_product foreign key (product_id) references products(id)
) engine=innodb;

create table product_tags (
    product_id bigint not null,
    tag_id     bigint not null,
    primary key (product_id, tag_id),
    constraint fk_product_tags_product foreign key (product_id) references products(id),
    constraint fk_product_tags_tag     foreign key (tag_id)     references tags(id)
) engine=innodb;

create table consignments (
    id                   bigint auto_increment primary key,
    created_date         datetime not null,
    updated_date         datetime not null,
    consignor_id         bigint   not null,
    received_by_id       bigint   not null,
    start_date           datetime,
    expiry_date          datetime,
    note                 text,
    consignment_status   varchar(50) default 'DRAFT',
    settled_at           datetime,
    closed_at            datetime,
    constraint fk_consignments_consignor    foreign key (consignor_id)    references users(id),
    constraint fk_consignments_received_by  foreign key (received_by_id)  references users(id)
) engine=innodb;

create table consignment_items (
    id                       bigint auto_increment primary key,
    created_date             datetime not null,
    updated_date             datetime not null,
    consignment_id           bigint   not null,
    product_id               bigint   not null,
    commission_rate          decimal(5, 2),
    consignment_item_status  varchar(50) default 'DRAFT',
    activated_at             datetime,
    constraint uq_consignment_items_product unique (product_id),
    constraint fk_consignment_items_consignment foreign key (consignment_id) references consignments(id),
    constraint fk_consignment_items_product     foreign key (product_id)     references products(id)
) engine=innodb;

create table price_schedules (
    id                     bigint auto_increment primary key,
    created_date           datetime not null,
    updated_date           datetime not null,
    consignment_item_id    bigint   not null,
    effective_after_days   int      not null default 0,
    price                  decimal(12, 0),
    applied_at             datetime,
    price_schedule_status  varchar(50) default 'PENDING',
    sequence_no            int      not null default 0,
    constraint fk_price_schedules_item foreign key (consignment_item_id) references consignment_items(id)
) engine=innodb;

create table item_dispositions (
    id                       bigint auto_increment primary key,
    created_date             datetime not null,
    updated_date             datetime not null,
    consignment_item_id      bigint   not null,
    item_disposition_type    varchar(50),
    item_disposition_status  varchar(50) default 'PENDING',
    pickup_deadline          datetime,
    consented_at             datetime,
    processed_at             datetime,
    processed_by             bigint,
    note                     text,
    constraint uq_item_dispositions_item unique (consignment_item_id),
    constraint fk_item_dispositions_item      foreign key (consignment_item_id) references consignment_items(id),
    constraint fk_item_dispositions_processor foreign key (processed_by)        references users(id)
) engine=innodb;