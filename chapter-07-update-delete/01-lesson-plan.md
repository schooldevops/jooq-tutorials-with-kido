# Chapter 07: Update & Delete 레벨 1 - 조건부 수정과 물리/논리 삭제

## 1. 커리큘럼 분석
* **제목:** Update & Delete: 조건부 수정과 삭제, 그리고 안정적인 물리/논리 삭제 구현
* **분석 내용:**
  * INSERT로 데이터를 쓰는 법을 익혔다면, 이제 데이터를 *수정(UPDATE)* 하고 *삭제(DELETE)* 하는 법을 배울 차례입니다.
  * jOOQ의 UPDATE/DELETE는 INSERT와 마찬가지로 **DSL 스타일**과 **UpdatableRecord 스타일** 두 가지로 접근할 수 있습니다.
  * 실무에서 매우 중요한 **논리 삭제(Soft Delete)** 패턴 — 데이터를 실제로 지우지 않고 삭제 시각을 기록하는 방식 — 도 함께 다룹니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **DSL UPDATE 체득:** `dsl.update(TABLE).set(FIELD, value).where(...).execute()` 방식으로 Type-Safe한 UPDATE를 빌드하고 실행할 수 있다.
2. **UpdatableRecord UPDATE 활용:** 기존 레코드를 조회한 뒤 필드를 변경하고 `store()` 를 호출하면 UPDATE가 자동 수행되는 Active Record 패턴을 이해한다.
3. **물리/논리 삭제 구분:** `deleteFrom(TABLE).where(...).execute()` 로 행을 완전히 제거하는 Hard Delete와, `deleted_at` 타임스탬프를 기록하는 Soft Delete의 차이와 구현법을 실습한다.

### 2.2. 학습 내용
1. **DSL 스타일 UPDATE:** `update()` → `set()` 체이닝 → `where()` 조건 → `execute()` 라이프사이클 시연.
2. **UpdatableRecord 스타일 UPDATE:** `fetchOne()` 로 레코드 조회 → 필드 변경 → `store()` 호출 시 자동 UPDATE 분기.
3. **Hard Delete (물리 삭제):** `deleteFrom(TABLE).where(조건).execute()` — 조건 없이 실행하면 전체 삭제 위험성 강조.
4. **Soft Delete (논리 삭제):** `deleted_at TIMESTAMP` 컬럼을 `LocalDateTime.now()` 로 UPDATE하여 삭제 표시. 조회 시 `deleted_at IS NULL` 조건 필터링 동반.
5. **시각화 (Mermaid):**
   * DSL UPDATE vs UpdatableRecord UPDATE 실행 흐름 시퀀스 다이어그램.
   * Hard Delete vs Soft Delete 상태 전이 다이어그램.

### 2.3. 기술 선택 및 개발환경 구성
* **언어:** Java 17+, Kotlin 1.9+
* **프레임워크:** Spring Boot 3.x (spring-boot-starter-jooq)
* **테스트 DB:** PostgreSQL 15 (Docker)
* **스키마 확장:** `book` 테이블에 `deleted_at TIMESTAMP` 컬럼 추가 (각 프로젝트 `schema.sql` 활용)
* **테스트 전략:** `@SpringBootTest` + `@Transactional` → 테스트 종료 시 자동 롤백

### 2.4. 실제 개발 및 테스트 단계 (강의 시연 항목)
1. DSL UPDATE: `dsl.update(BOOK).set(BOOK.TITLE, "New Title").where(BOOK.ID.eq(1)).execute()` 결과가 1인지 검증
2. UpdatableRecord UPDATE: `dsl.fetchOne(AUTHOR, AUTHOR.ID.eq(1))` 조회 → `setFirstName("Updated")` → `store()` 후 변경 확인
3. Hard Delete: `dsl.deleteFrom(BOOK).where(BOOK.ID.eq(id)).execute()` 후 조회 결과가 null 검증
4. Soft Delete: `dsl.update(BOOK).set(BOOK.DELETED_AT, LocalDateTime.now()).where(BOOK.ID.eq(id)).execute()` 후 `deleted_at IS NOT NULL` 검증
5. **목표:** 작성된 모든 수정/삭제가 단위 테스트 코드 상에서 실행되고 통과해야 함. (테스트 후 자동 롤백 적용)
