CREATE TABLE IF NOT EXISTS daily_sales (
    id SERIAL PRIMARY KEY,
    sale_date DATE NOT NULL,
    product_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    revenue NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    updated_at TIMESTAMP NULL
);

CREATE INDEX IF NOT EXISTS idx_daily_sales_date_category ON daily_sales (sale_date, category);