create table users(
    id bigint auto_increment primary key,
    created_date DATETIME not null,
    updated_date DATETIME not null,
    full_name VARCHAR(255) not null,
    email VARCHAR(255) not null,
    phone_number varchar(255),
    password_hash varchar(255),
    address varchar(255),
    avatar_url varchar(255),
    avatar_public_id varchar(255),
    role varchar(50) not null,
    status varchar(50) not null,

    constraint uq_users_email unique (email)
) engine=innodb;

create table categories (
    id bigint auto_increment primary key,
    created_date datetime not null,
    updated_date datetime not null,
    name varchar(255) not null,
    slug varchar(255) not null,
    active boolean not null default true,
    parent_id bigint,

    constraint uq_categories_name unique (name),
    constraint uq_categories_slug unique (slug),
    constraint fk_categories_parent foreign key (parent_id) references categories(id)
)engine=innodb;

create table tags(
    id bigint auto_increment primary key,
    created_date datetime not null,
    updated_date datetime not null,
    name varchar(255) not null,
    slug varchar(255) not null,
    active boolean not null default true,

    constraint uq_tags_name unique (name),
    constraint uq_tags_slug unique (slug)
) engine=innodb;

create table brands(
    id bigint auto_increment primary key,
    created_date datetime not null,
    updated_date datetime not null,
    name varchar(255) not null,
    slug varchar(255) not null,
    description TEXT,
    active boolean not null default true,

    constraint uq_brands_name unique (name),
    constraint uq_brands_slug unique (slug)
) engine=innodb;

create table refresh_tokens(
    id bigint auto_increment primary key,
    created_date datetime not null,
    updated_date datetime not null,
    refresh_token varchar(500) not null,
    expires_at datetime not null,
    user_id bigint not null,
    revoked boolean not null default false,

    constraint uq_refresh_tokens_refresh_token unique (refresh_token),
    constraint fk_refresh_tokens foreign key (user_id) references users
)