# Chapter 09: 정렬과 페이징 - 실습 개발 플랜

## 1. 실습 목적
* `orderBy().limit().offset()` 조합으로 단일/다중 정렬 및 Offset 페이징 쿼리를 구현하고 테스트로 검증합니다.
* `@Sql` 어노테이션으로 테스트 전용 데이터를 삽입하여 페이징 경계 케이스를 검증합니다.

## 2. 사전 준비 (환경 구성)

### Step 1: 기본 프로젝트 복사 및 설정 빌드업
* `codebase/java-spring` → `chapter-09-sorting-paging/code-java`
* `codebase/kotlin-spring` → `chapter-09-sorting-paging/code-kotlin`
* `build.gradle`: jOOQ 설정 추가 (이전 챕터와 동일)
* `application.yml`: datasource `jooq_demo` DB

### Step 2: 테스트 데이터 보강
페이징 테스트를 위해 `src/test/resources/test-data.sql` 작성:
```sql
-- 기존 데이터 외 추가 Book 데이터 삽입
INSERT INTO book (title, author_id, published_year) VALUES ('Anna Karenina', 1, 1877);
INSERT INTO book (title, author_id, published_year) VALUES ('Emma', 2, 1815);
INSERT INTO book (title, author_id, published_year) VALUES ('Sense and Sensibility', 2, 1811);
INSERT INTO book (title, author_id, published_year) VALUES ('Othello', 1, 1603);
```
* `@Sql(scripts = "/test-data.sql", executionPhase = BEFORE_TEST_METHOD)` 로 각 테스트 메서드 전에 삽입

## 3. 개발 플랜 (BDD 테스트 시나리오)

### Step 3: Java - JooqSortingPagingRepository 및 테스트

**`JooqSortingPagingRepository.java` 구성:**

| 메서드 | 정렬 | 페이징 |
|---|---|---|
| `findBooksOrderedByTitle()` | TITLE ASC | 없음 |
| `findBooksOrderedByYearDesc()` | PUBLISHED_YEAR DESC | 없음 |
| `findBooksWithPaging(page, size)` | TITLE ASC | limit+offset |
| `findBooksWithMultiSort(page, size)` | YEAR DESC, TITLE ASC | limit+offset |

**단위 테스트:**
```
테스트 1: findBooksOrderedByTitle
  → 반환 리스트가 비어있지 않고, title 순서가 오름차순임을 검증

테스트 2: findBooksOrderedByYearDesc
  → 반환 리스트의 첫 번째 요소가 최신 연도임을 검증

테스트 3: findBooksWithPaging(page=0, size=2)
  → 반환 결과가 최대 2개임을 검증

테스트 4: findBooksWithMultiSort(page=0, size=2)
  → 결과가 최대 2개이고, 연도 기준 내림차순임을 검증
```

---

### Step 4: Kotlin - JooqSortingPagingRepository 및 테스트
* Java와 동일한 4개 메서드 및 테스트 시나리오

---

## 4. 실행 체크리스트

1. Docker 컨테이너 실행 확인
2. Java: `./gradlew test`
3. Kotlin: `./gradlew test`
4. 모든 4개 테스트 GREEN 확인
