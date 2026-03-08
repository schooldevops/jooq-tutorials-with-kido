# 실전 샘플 프로젝트 1: 회원/권한 시스템 (RBAC) 아키텍처 및 스키마 설계

## 1. 개요 (Overview)
대부분의 엔터프라이즈 애플리케이션 및 B2B 서비스에서는 **RBAC (Role-Based Access Control)** 모델을 통해 리소스 접근 권한을 관리합니다. 사용자는 하나 이상의 역할을(Role) 부여받고, 각 역할은 여러 권한(Permission)을 포함하는 N:M 구조입니다.

이 장에서는 복잡한 다대다 관계를 jOOQ 의 강력한 조인(Join) 스펙과 `multiset` 기능을 활용하여 효율적으로 조회하고, 권한 유무를 체크하는 실전 코드를 작성합니다.

## 2. 데이터베이스 스키마 설계 (ERD)

RBAC 모델의 핵심 5개 테이블 스키마입니다.

```sql
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
    action VARCHAR(50) NOT NULL,   -- 행위 (예: READ, WRITE, DELETE)
    description VARCHAR(255),
    UNIQUE (resource, action)
);

-- 4. 사용자-역할 매핑 테이블 (N:M)
CREATE TABLE user_role (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES role(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- 5. 역할-권한 매핑 테이블 (N:M)
CREATE TABLE role_permission (
    role_id BIGINT REFERENCES role(id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);
```

## 3. 핵심 비즈니스 요구사항 및 쿼리 포인트

실전 프로젝트에서 가장 빈번하게 요구되는 기능은 다음과 같습니다.

### 3.1. 인증/인가 시 사용자 전체 권한(Permission) 프로필 조회
- 로그인(인증)에 성공한 후 세션이나 JWT를 발급하기 위해, 해당 사용자가 가진 **모든 역할과 권한**을 계층적으로 불러와야 합니다.
- **jOOQ 구현 포인트:** `MULTISET` 연산자를 활용하여 `UserDTO` 안에 `List<RoleDTO>` 를 담고, 각 요소 안에 `List<PermissionDTO>` 를 매핑하여 **단일 쿼리(1-Query)**로 계층 데이터를 조회합니다. (N+1 문제 원천 차단)

### 3.2. 특정 리소스에 대한 접근 권한(hasPermission) 검사
- 사용자가 `ARTICLE`을 `DELETE` 하려고 할 때, 권한이 있는지 boolean으로 확인합니다.
- **jOOQ 구현 포인트:** `EXISTS` 구문을 사용하여 5개 테이블을 JOIN 한 뒤, `LIMIT 1` 기반의 빠른 존재 여부 체크 쿼리를 작성합니다.

### 3.3. 역할(Role)별 보유 권한 관리 (Backoffice API)
- 관리자가 특정 역할(예: MANAGER) 패키지에 새로운 권한을 추가하거나 제외할 수 있습니다.
- **jOOQ 구현 포인트:** 대량의 데이터를 매핑하는 `INSERT ... ON CONFLICT DO NOTHING` (Upsert 패턴) 또는 벌크 인서트/딜리트를 사용하여 성능을 최적화합니다.

## 4. 아키텍처 (Layered Architecture)

* **Controller Layer:** 권한 체크 어노테이션(`@PreAuthorize` 등) 또는 인터셉터와 결합하여 라우팅.
* **Service Layer:** `UserService`, `RolePermissionService`로 비즈니스 로직 격리. 보안 로직과 일반 조회 로직을 분리.
* **Repository Layer:** 순수 jOOQ 기반의 `UserQueryRepository`, `PermissionQueryRepository` 를 통해 복잡한 권한 N:M 구조를 Fetch.
