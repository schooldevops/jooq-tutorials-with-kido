-- 1. 사용자 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, SUSPENDED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 역할 테이블 (예: ADMIN, MANAGER, USER)
CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 3. 권한 테이블 (예: READ_ARTICLE, WRITE_ARTICLE, DELETE_USER)
CREATE TABLE permission (
    id BIGSERIAL PRIMARY KEY,
    resource VARCHAR(50) NOT NULL, -- 대상 리소스 (예: ARTICLE, USER)
    action VARCHAR(50) NOT NULL, -- 행위 (예: READ, WRITE, DELETE)
    description VARCHAR(255),
    UNIQUE (resource, action)
);

-- 4. 사용자-역할 매핑 테이블 (N:M)
CREATE TABLE user_role (
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES role (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- 5. 역할-권한 매핑 테이블 (N:M)
CREATE TABLE role_permission (
    role_id BIGINT REFERENCES role (id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permission (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);