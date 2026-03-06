# Chapter 06: Insert (데이터 삽입의 두 가지 방식) - 실습 개발 플랜

## 1. 실습 목적
* `DSLContext`의 DSL 스타일 INSERT와 `UpdatableRecord` 스타일 INSERT 두 가지 방식을 각각 구현하고 테스트로 검증합니다.
* `INSERT ... RETURNING` 절을 활용하여 DB가 자동 생성한 PK를 회수하는 실전 패턴을 코드로 증명합니다.

## 2. 사전 준비 (환경 구성)
> **중요:** Chapter 02에서 만든 docker-compose.yml이 구동 중이어야 합니다.

### Step 1: 기본 프로젝트 복사 및 설정 빌드업
* `codebase/java-spring` → `chapter-06-insert/code-java` 로 복사
* `codebase/kotlin-spring` → `chapter-06-insert/code-kotlin` 로 복사
* **빌드 설정 추가 (`build.gradle`):**
  * `nu.studer.jooq` 9.0 플러그인 적용
  * DB 접근 정보: `localhost:5432/jooq_demo`, user=postgres, pw=postgres
  * `generateSchemaSourceOnCompilation = true`

## 3. 개발 플랜 (분할 정복 기반 BDD 테스트 시나리오)

### Step 2: Java 환경 - JooqInsertRepository 및 단위 테스트 작성

**`JooqInsertRepository.java` 구성:**

| 메서드 | INSERT 방식 | 설명 |
|---|---|---|
| `insertAuthorDsl(firstName, lastName)` | DSL 스타일 | 영향 행 수(int) 반환 |
| `insertAuthorWithRecord(firstName, lastName)` | UpdatableRecord | 저장된 `AuthorRecord` 반환 (generated id 포함) |
| `insertBookReturningId(title, authorId, year)` | DSL + RETURNING | 생성된 `BOOK.ID` (Integer) 반환 |

**단위 테스트 (`JooqInsertRepositoryTest.java`):**

```
테스트 1: insertAuthorDsl
  Given: firstName="Leo", lastName="Tolstoy"
  When:  insertAuthorDsl("Leo", "Tolstoy") 호출
  Then:  반환 값이 1 (영향 행 수)

테스트 2: insertAuthorWithRecord
  Given: firstName="Franz", lastName="Kafka"
  When:  insertAuthorWithRecord("Franz", "Kafka") 호출
  Then:  반환된 레코드의 getId()가 null이 아님 (generated PK 확인)

테스트 3: insertBookReturningId
  Given: 기존 AUTHOR 중 하나의 id, title="The Trial", year=1925
  When:  insertBookReturningId("The Trial", authorId, 1925) 호출
  Then:  반환된 id가 0보다 큰 양수
```

* `@Transactional` 적용 → 테스트 종료 시 자동 롤백 (DB 클린 유지)

---

### Step 3: Kotlin 환경 - JooqInsertRepository 및 단위 테스트 작성

**`JooqInsertRepository.kt` 구성:**
* Java와 동일한 세 가지 메서드를 Kotlin 문법으로 구현
* `apply {}` 블록으로 UpdatableRecord 초기화를 더 Kotlin스럽게 작성

**단위 테스트 (`JooqInsertRepositoryTest.kt`):**
* Java 테스트와 동일한 시나리오 3개
* `@SpringBootTest` + `@Transactional` 구조 동일

---

## 4. 실행 체크리스트

본 플랜 작성 완료 후, EXECUTION 모드로 전환하여:

1. Docker 컨테이너 실행 확인 (`docker ps | grep jooq`)
2. Java 프로젝트: `./gradlew test` 실행
3. Kotlin 프로젝트: `./gradlew test` 실행
4. 모든 3개 테스트가 GREEN 확인 후 Docker 유지 (다음 챕터 연속 활용)
