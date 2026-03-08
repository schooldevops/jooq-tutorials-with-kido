# 레거시 DB 마이그레이션 툴 단계별 구현 안내

이 단계별 구현 가이드는 jOOQ를 이용한 이기종 DB 데이터 안전 이동 도구를 구현하는 방법을 정리했습니다.

## Step 1: 다중 DB 연결 및 Flyway 설정
*   `application.yml` 에 `spring.datasource.legacy` 와 `spring.datasource.target` 등 두 개의 접속 정보를 정의합니다.
*   `build.gradle` 스크립트를 수정하여 `jdbc` 연결 문자열을 바꿔가며 두 번의 jOOQ Generate를 수행하도록 구성하거나, 코드를 별도로 구성합니다. 본 예제에서는 각기 다른 스키마 이름과 접속 계정을 사용하여 jooq 패키지를 분리 생성(`com.example.jooq.legacy`, `com.example.jooq.target`) 되도록 할 수 있습니다.

## Step 2: 레거시 및 타겟 스키마 V1 Script 작성
*   **Legacy**: `users_v1`, `orders_v1` 테이블 생성 쿼리를 작성합니다.
*   **Target**: `member`, `purchase_order` 테이블 생성 쿼리를 작성합니다.

## Step 3: jOOQ Code Generation 구성
*   `build.gradle` 의 `nu.studer.jooq` 블록 내에서 `legacy` execution 과 `target` execution 을 분리하여 2번 Generate 합니다.

## Step 4: 마이그레이션 로직 구현
*   도메인 패키지 하위에 `MigrationService` 작성.
*   데이터 정합성을 보장하기 위해 레거시 도메인(`UsersV1Record`) 을 꺼내, 비즈니스 로직(문자열 분리, Enum 변경)을 거친 후 타겟 도메인(`MemberRecord`) 객체로 매핑합니다.
*   `dsl.batchInsert(memberRecords).execute()` 같은 형태로 Bulk insert 최적화를 적용합니다.

## Step 5: 통합 테스트를 통한 입증
*   테스트 코드에서 `legacy` DB 에 100건 이상의 Mock 데이터를 준비합니다.
*   `migrationService.migrateAll()` 호출.
*   `target` DB 에 모든 레코드가 정상적으로 적재되고 컬럼 변환이 문제없이 적용되었는지 검증합니다.
