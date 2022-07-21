create table material_order
(
    id             bigserial    not null primary key,
    name           varchar(100) not null unique,
    delivery_price decimal,
    shop           varchar(100) not null,
    purchase_date  timestamp
);

create table material
(
    id                bigserial not null primary key,
    name              varchar(1000),
    price             decimal,
    delivery          decimal,
    number            int,
    image_url         varchar(300),
    material_order_id bigserial not null,
    FOREIGN KEY (material_order_id) REFERENCES material_order (id)
);

create table jewelry_material
(
    jewelry_id  bigserial not null,
    material_id bigserial not null,
    number      int       not null,
    PRIMARY KEY (jewelry_id, material_id),
    FOREIGN KEY (material_id) REFERENCES material (id)
);

CREATE SEQUENCE user_id_seq;
CREATE TABLE my_user
(
    user_id  integer   NOT NULL DEFAULT nextval('user_id_seq'),
    username varchar(45) NOT NULL,
    password varchar(64) NOT NULL,
    role     varchar(45) NOT NULL,
    enabled  int DEFAULT NULL,
    PRIMARY KEY (user_id)
);

INSERT INTO my_user (username, password, role, enabled)
VALUES ('admin', '$2a$12$H4CEKF/.w6pm0az60ddUVuPB21vKug9tw3It6eVHOy5ckJuvA.BEe', 'ROLE_USER', 1);
