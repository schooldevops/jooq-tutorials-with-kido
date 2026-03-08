# 5. 레거시 DB 마이그레이션 툴 구조 설계

이 문서는 "레거시 DB 마이그레이션 도구(Legacy DB Migration Tool)"의 아키텍처와 jOOQ를 사용한 이기종 스키마 데이터 이전 전략을 정리합니다.

## 1. 아키텍처 개요
본 프로젝트는 구버전의 스키마(`legacy`)를 가진 데이터베이스에서 최신 버전에 호환되는 새로운 스키마(`target`)로 대량의 데이터를 마이그레이션 하는 도구 및 패턴을 다룹니다.

*   **Source DB**: `jooq_migration_legacy`
    *   `users_v1`: id, full_name, user_status(varchar), created_date
    *   `orders_v1`: id, user_id, order_total, state(varchar)
*   **Target DB**: `jooq_migration_target`
    *   `member`: id, first_name, last_name, status(enum), created_at
    *   `purchase_order`: id, member_id, total_amount, order_state(enum)

### 2. 마이그레이션 흐름(ETL)
마이그레이션 도구는 Extract, Transform, Load(ETL) 패턴을 따릅니다.

1.  **Extract (추출)**: Legacy DB 환경과 연결된 jOOQ Context(`legacyDsl`)를 사용하여 기존 테이블에서 벌크 단위(Chunk)로 데이터를 조회합니다.
2.  **Transform (변환)**:
    *   `full_name`을 `first_name`과 `last_name`으로 분리.
    *   `user_status` (문자열) 값을 Enum(예: `ACTIVE`, `INACTIVE`) 객체로 파싱 및 맵핑.
3.  **Load (적재)**: Target DB 환경과 연결된 jOOQ Context(`targetDsl`)를 사용하여 변환된 객체 컬렉션을 `batchInsert` 등의 기능으로 타겟 스키마에 고속으로 Insert합니다.

### 3. jOOQ 멀티 스키마 연결 전략
*   **Flyway & DataSources**: Spring Boot 내에서 두 개의 `DataSource`(Legacy, Target)를 정의하거나, 최소한 각 마이그레이션 별로 서로 다른 connection URL을 가집니다.
*   **Code Generation**: 프로젝트 내의 `build.gradle`에서 서로 다른 두 스키마(`legacy`, `target`)의 코드를 각각 다른 패키지로 Generate 하여, 타입 충돌을 피하고 안정적인 컴파일 타임 검증을 강제합니다.
