# Chapter 07: Update & Delete - 실습 개발 플랜

## 1. 실습 목적
* `DSLContext`의 DSL 스타일 UPDATE / DELETE와 `UpdatableRecord` 스타일 UPDATE, 그리고 Soft Delete 패턴을 각각 구현하고 테스트로 검증합니다.
* `deleted_at TIMESTAMP` 컬럼을 이용한 논리 삭제 패턴이 실제 DB 레벨에서 올바르게 동작함을 테스트 코드로 증명합니다.

## 2. 사전 준비 (환경 구성)
> **중요:** Chapter 02에서 만든 docker-compose.yml이 구동 중이어야 합니다.

### Step 1: 기본 프로젝트 복사 및 설정 빌드업
* `codebase/java-spring` → `chapter-07-update-delete/code-java` 복사
* `codebase/kotlin-spring` → `chapter-07-update-delete/code-kotlin` 복사
* **빌드 설정 추가 (`build.gradle`):** `nu.studer.jooq` 9.0 플러그인 + DB 접근 정보 (이전 챕터와 동일)
* **스키마 확장:** `src/main/resources/schema.sql` 에 `ALTER TABLE book ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;` 추가, `application.yml`에서 `spring.sql.init.mode: always` 설정

## 3. 개발 플랜 (분할 정복 기반 BDD 테스트 시나리오)

### Step 2: Java 환경 - JooqUpdateDeleteRepository 및 단위 테스트 작성

**`JooqUpdateDeleteRepository.java` 구성:**

| 메서드 | UPDATE/DELETE 방식 | 설명 |
|---|---|---|
| `updateBookTitle(bookId, newTitle)` | DSL UPDATE | 영향 행 수(int) 반환 |
| `updateAuthorWithRecord(authorId, newFirstName)` | UpdatableRecord | 수정된 `AuthorRecord` 반환 |
| `deleteBookById(bookId)` | Hard Delete | 영향 행 수(int) 반환 |
| `softDeleteBook(bookId)` | Soft Delete | `deleted_at` 설정, 영향 행 수(int) 반환 |

**단위 테스트 (`JooqUpdateDeleteRepositoryTest.java`):**

```
테스트 1: updateBookTitle
  Given: 기존 book id, newTitle = "Hamlet (2nd Edition)"
  When:  updateBookTitle(bookId, newTitle) 호출
  Then:  반환 값이 1, 재조회 시 TITLE이 newTitle과 일치

테스트 2: updateAuthorWithRecord
  Given: 기존 author id, newFirstName = "Bill"
  When:  updateAuthorWithRecord(authorId, newFirstName) 호출
  Then:  반환된 레코드의 getFirstName()이 newFirstName과 일치

테스트 3: deleteBookById
  Given: 새로 삽입한 book의 id
  When:  deleteBookById(bookId) 호출
  Then:  반환 값이 1, 재조회 결과가 null

테스트 4: softDeleteBook
  Given: 새로 삽입한 book의 id
  When:  softDeleteBook(bookId) 호출
  Then:  반환 값이 1, 재조회 시 deleted_at이 null이 아님
```

* `@Transactional` 적용 → 테스트 종료 시 자동 롤백

---

### Step 3: Kotlin 환경 - JooqUpdateDeleteRepository 및 단위 테스트 작성

**`JooqUpdateDeleteRepository.kt` 구성:**
* Java와 동일한 네 가지 메서드, Kotlin 문법 적용
* `let {}` 블록으로 null-safe한 UpdatableRecord 처리

**단위 테스트 (`JooqUpdateDeleteRepositoryTest.kt`):**
* Java 테스트와 동일한 시나리오 4개, Kotlin assertj 활용

---

## 4. 실행 체크리스트

1. Docker 컨테이너 실행 확인 (`jooq_demo_postgres Up`)
2. `schema.sql` 적용 확인 (deleted_at 컬럼 존재 여부)
3. Java 프로젝트: `./gradlew test` 실행
4. Kotlin 프로젝트: `./gradlew test` 실행
5. 모든 4개 테스트가 GREEN 확인
