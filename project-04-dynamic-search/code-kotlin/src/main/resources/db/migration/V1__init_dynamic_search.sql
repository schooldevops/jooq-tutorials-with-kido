CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_status VARCHAR(50) NOT NULL, -- e.g., IN_STOCK, OUT_OF_STOCK
    status VARCHAR(50) NOT NULL, -- e.g., ACTIVE, INACTIVE, DELETED
    manufacturer VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_product_category ON product (category);

CREATE INDEX IF NOT EXISTS idx_product_status ON product (status);

CREATE INDEX IF NOT EXISTS idx_product_price ON product (price);