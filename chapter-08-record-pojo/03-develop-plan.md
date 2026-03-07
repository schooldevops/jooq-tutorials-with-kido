# Chapter 08: Record와 POJO - 실습 개발 플랜

## 1. 실습 목적
* jOOQ 결과 매핑의 네 가지 전략(`Record`, `fetchInto`, `fetchMap`, `map 람다`)을 각각 구현하고 테스트로 검증합니다.
* 커스텀 DTO(`BookSummaryDto`)에 JOIN 결과를 매핑하는 패턴을 단위 테스트로 증명합니다.

## 2. 사전 준비 (환경 구성)
> **중요:** `jooq_demo_postgres` Docker 컨테이너가 구동 중이어야 합니다. (deleted_at 컬럼 포함 스키마)

### Step 1: 기본 프로젝트 복사 및 설정 빌드업
* `codebase/java-spring` → `chapter-08-record-pojo/code-java` 복사
* `codebase/kotlin-spring` → `chapter-08-record-pojo/code-kotlin` 복사
* `build.gradle`: jOOQ 설정 추가 (이전 챕터와 동일)
* `settings.gradle`: `rootProject.name` 변경
* `application.yml`: datasource를 `jooq_demo` DB로 변경

## 3. 개발 플랜 (BDD 기반 테스트 시나리오)

### Step 2: 커스텀 DTO 작성
* **Java:** `BookSummaryDto.java` (Java 16+ `record`)
* **Kotlin:** `BookSummaryDto.kt` (`data class`)

### Step 3: Java 환경 - JooqRecordPojoRepository 및 단위 테스트

**`JooqRecordPojoRepository.java` 구성:**

| 메서드 | 반환 타입 | 매핑 방식 |
|---|---|---|
| `fetchAllAsRecord()` | `List<BookRecord>` | `selectFrom(BOOK).fetch()` |
| `fetchAllAsPojo()` | `List<Book>` | `fetchInto(Book.class)` |
| `fetchAsMap()` | `Map<Integer, String>` | `fetchMap(BOOK.ID, BOOK.TITLE)` |
| `fetchIntoCustomDto()` | `List<BookSummaryDto>` | JOIN + `fetch(r -> new DTO(...))` |

**단위 테스트 (`JooqRecordPojoRepositoryTest.java`):**
```
테스트 1: fetchAllAsRecord
  Given: init-script seeded data (2 books)
  When:  fetchAllAsRecord() 호출
  Then:  반환 리스트가 null이 아니고 비어있지 않음, 각 레코드의 title이 null이 아님

테스트 2: fetchAllAsPojo
  Given: seeded data
  When:  fetchAllAsPojo() 호출
  Then:  반환 리스트가 null이 아님, 각 POJO의 title이 null이 아님

테스트 3: fetchAsMap
  Given: seeded data
  When:  fetchAsMap() 호출
  Then:  반환 Map이 비어있지 않음, key=1 에 해당하는 값이 "Hamlet"

테스트 4: fetchIntoCustomDto
  Given: seeded data (author + book JOIN)
  When:  fetchIntoCustomDto() 호출
  Then:  반환 리스트가 비어있지 않음, authorLastName이 null이 아님
```

---

### Step 4: Kotlin 환경 - JooqRecordPojoRepository 및 단위 테스트

**`JooqRecordPojoRepository.kt` 구성:**
* Java와 동일한 네 가지 메서드
* Kotlin `fetch { BookSummaryDto(it[BOOK.ID], ...) }` 람다 스타일

**단위 테스트 (`JooqRecordPojoRepositoryTest.kt`):**
* Java와 동일한 4개 시나리오

---

## 4. 실행 체크리스트

1. Docker 컨테이너 실행 확인
2. Java: `./gradlew test`
3. Kotlin: `./gradlew test`
4. 모든 4개 테스트 GREEN 확인
