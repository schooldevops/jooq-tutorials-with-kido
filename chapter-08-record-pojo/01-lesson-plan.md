# Chapter 08: Record와 POJO 레벨 1 - 결과 매핑의 네 가지 전략

## 1. 커리큘럼 분석
* **제목:** Record와 POJO: `fetchInto()`, `Map` 처리, 자동 생성된 POJO 클래스 활용법
* **분석 내용:**
  * SELECT 쿼리로 데이터를 가져왔을 때, 결과를 어떤 형태로 받아 쓸 것인지가 실무의 핵심입니다.
  * jOOQ는 DB 결과(ResultSet)를 `Record`, `POJO`, `Map`, 또는 커스텀 DTO 등 다양한 형태로 변환하는 유연한 API를 제공합니다.
  * 각 방식의 장단점을 이해하고, 상황에 맞게 최적의 변환 전략을 선택할 수 있는 역량을 기릅니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **Record 타입 이해:** jOOQ가 자동 생성하는 `AuthorRecord`, `BookRecord` 타입이 무엇인지, 왜 유용한지 이해한다.
2. **`fetchInto()` 활용:** `fetchInto(Class<T>)` 로 자동 생성 POJO 또는 커스텀 DTO에 결과를 매핑하는 방법을 익힌다.
3. **`fetchMap()` 활용:** `fetchMap(keyField, valueField)` 로 SQL 결과를 `Map<K, V>` 형태로 직접 변환한다.
4. **람다 매핑 (`map()`):** `.map(r -> new Dto(...))` 패턴으로 JOIN 결과 등 복잡한 데이터를 원하는 DTO로 직접 조립한다.

### 2.2. 학습 내용
1. **Record vs POJO:** `AuthorRecord`(UpdatableRecord)와 `Author`(POJO)의 구조적 차이 및 사용 목적 비교.
2. **`selectFrom` + `fetchInto`:** 테이블 전체 조회 후 POJO 리스트로 변환하는 가장 일반적인 패턴.
3. **`fetchMap`:** `Map<BOOK.ID 타입, BOOK.TITLE 타입>` 형태로 빠른 Key-Value 조회가 필요할 때 활용.
4. **람다 `map` + JOIN + 커스텀 DTO:** `AUTHOR`와 `BOOK` 테이블을 JOIN한 뒤 `BookSummaryDto`로 변환.
5. **시각화 (Mermaid):** fetchInto 데이터 변환 파이프라인 다이어그램.

### 2.3. 기술 선택 및 개발환경 구성
* **언어:** Java 17+ (record 사용), Kotlin 1.9+ (data class 사용)
* **프레임워크:** Spring Boot 3.x (spring-boot-starter-jooq)
* **테스트 DB:** PostgreSQL 15 (Docker, jooq_demo)
* **커스텀 DTO:** Java `record BookSummaryDto(...)`, Kotlin `data class BookSummaryDto(...)`
* **테스트 전략:** `@SpringBootTest` + `@Transactional` 자동 롤백

### 2.4. 실제 개발 및 테스트 단계 (강의 시연 항목)
1. `fetchAllAsRecord()`: `selectFrom(BOOK).fetch()` → `List<BookRecord>` 반환, 각 레코드 필드 접근 시연
2. `fetchAllAsPojo()`: `selectFrom(BOOK).fetchInto(Book.class)` → `List<Book>` 반환, POJO 필드 접근 시연
3. `fetchAsMap()`: `select(BOOK.ID, BOOK.TITLE).from(BOOK).fetchMap(BOOK.ID, BOOK.TITLE)` → `Map<Integer, String>` 시연
4. `fetchIntoCustomDto()`: AUTHOR JOIN BOOK 후 라다 매핑으로 `BookSummaryDto` 리스트 반환
5. **목표:** 모든 조회가 단위 테스트에서 실행되고 통과. 롤백 적용.
