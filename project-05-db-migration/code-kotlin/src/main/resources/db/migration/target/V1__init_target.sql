CREATE TABLE IF NOT EXISTS member (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL, -- Will map to Enum in App
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_state VARCHAR(50) NOT NULL, -- Will map to Enum in App
    CONSTRAINT fk_po_member FOREIGN KEY (member_id) REFERENCES member (id)
);