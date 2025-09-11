CREATE SEQUENCE products_seq START 100 INCREMENT 1;

CREATE TABLE products
(
    id                 INT8             NOT NULL,
    product_name       VARCHAR(255)     NOT NULL,
    price              numeric(38,2)    NOT NULL,
    description        VARCHAR(255)     NOT NULL,
    user_id            INT8             NOT NULL REFERENCES users,
    PRIMARY KEY (id)
)
CREATE INDEX products_user_id_idx ON products (user_id);