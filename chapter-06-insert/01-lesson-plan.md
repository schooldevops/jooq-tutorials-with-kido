# Chapter 06: Insert 레벨 1 - 데이터 삽입의 두 가지 방식

## 1. 커리큘럼 분석
* **제목:** Insert: 삽입 (DSL 스타일 삽입과 `UpdatableRecord`를 활용한 데이터 추가)
* **분석 내용:**
  * 이전 챕터까지 데이터를 *읽는* 법(SELECT)을 완전히 익혔다면, 이제는 데이터를 *쓰는* 첫 번째 단계인 INSERT를 배울 차례입니다.
  * jOOQ의 INSERT는 크게 두 가지 철학으로 구현할 수 있습니다. 하나는 SQL 문장을 완전히 코드로 재현하는 **DSL 스타일**, 다른 하나는 테이블 Row 자체를 Java/Kotlin 객체처럼 다루는 **UpdatableRecord 스타일**입니다.
  * 두 방식의 차이를 명확히 이해하고, 상황에 맞게 선택할 수 있는 역량을 기릅니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **DSL INSERT 체득:** `dsl.insertInto(TABLE).set(FIELD, value)...execute()` 방식으로 Type-Safe한 INSERT 쿼리를 빌드하고 실행할 수 있다.
2. **UpdatableRecord 활용:** `dsl.newRecord(TABLE)` 로 레코드 객체를 생성하여 `.set()` 후 `.store()` 또는 `.insert()` 로 저장하는 Active Record 패턴을 이해하고 활용할 수 있다.
3. **INSERT ... RETURNING 활용:** `returning()` + `fetchOne()` 조합으로 DB가 자동 생성한 Primary Key(SERIAL/SEQUENCE 값)를 즉시 회수하는 방법을 알 수 있다.

### 2.2. 학습 내용
1. **DSL 스타일 INSERT:** `insertInto()` → `set()` 체이닝 → `execute()` 호출 라이프사이클 시연. 반환 타입이 `int`(영향 행 수)임을 확인.
2. **UpdatableRecord 스타일 INSERT:** `newRecord(AUTHOR)` 로 레코드를 생성하고, 필드를 채운 뒤 `store()` 를 호출하면 자동으로 INSERT가 수행되고, 레코드에 DB generated 값(id 등)이 채워짐을 확인.
3. **INSERT ... RETURNING:** `.returning(AUTHOR.ID)` 혹은 `.returning()` 으로 방금 삽입된 행의 컬럼 값을 즉시 반환받는 방식 시연.
4. **시각화 (Mermaid):**
   * DSL INSERT vs UpdatableRecord INSERT의 실행 흐름 비교 시퀀스 다이어그램.
   * `UpdatableRecord` 상태 전이(New → Stored) 다이어그램.

### 2.3. 기술 선택 및 개발환경 구성
* **언어:** Java 17+, Kotlin 1.9+
* **프레임워크:** Spring Boot 3.x (spring-boot-starter-jooq)
* **테스트 DB:** PostgreSQL 15 (Docker)
* **사용 클래스:** `DSLContext`, `AuthorRecord`, `BookRecord` (jOOQ auto-generated)
* **테스트 전략:** `@SpringBootTest` + `@Transactional` → 테스트 종료 시 자동 롤백으로 데이터 오염 방지

### 2.4. 실제 개발 및 테스트 단계 (강의 시연 항목)
1. DSL INSERT: `dsl.insertInto(AUTHOR).set(AUTHOR.FIRST_NAME, "Leo").set(AUTHOR.LAST_NAME, "Tolstoy").execute()` 결과가 1인지 검증
2. UpdatableRecord INSERT: `newRecord(AUTHOR)` 로 레코드 생성 → `store()` 호출 후 `record.getId()` 가 null이 아닌지 검증
3. INSERT RETURNING: `insertInto(BOOK)...returning(BOOK.ID).fetchOne()` 으로 생성된 PK 값을 직접 추출 검증
4. **목표:** 작성된 모든 삽입이 단위 테스트 코드 상에서 실행되고 통과해야 함. (테스트 후 자동 롤백 적용)
