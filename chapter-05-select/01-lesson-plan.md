# Chapter 05: Select 레벨 1 - 기본 조회와 조건 제어

## 1. 커리큘럼 분석
* **제목:** Select: 조회 (`select()`, `from()`, `where()` 기본 조회 및 별칭(Alias) 활용)
* **분석 내용:**
  * 이전 챕터에서 구축한 `DSLContext`와 자동 생성된 jOOQ 클래스들을 바탕으로, 가장 기본적이고 빈번하게 쓰이는 DQL(Data Query Language)인 `SELECT` 문법을 다룹니다.
  * SQL을 작성하듯 자연스럽게 이어지는 jOOQ의 Fluent API 구조를 익히고, Type-Safe 한 `where` 조건절 객체(`Condition`)를 다루는 요령을 체득합니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **기본 쿼리 구조 체득:** `DSLContext`의 `select()`, `from()`, `where()`, `fetch()` 체이닝 구조를 이해하고 기본 조회를 수행할 수 있다.
2. **Type-Safe 조건식(Condition) 활용:** 자동 생성된 Table 클래스의 필드(Column) 객체가 제공하는 `.eq()`, `.in()`, `> (gt)` 등의 조건 메서드를 안전하게 다룰 수 있다.
3. **별칭(Alias)과 조인 맛보기:** 테이블 혹은 컬럼에 별칭을 부여하는 `.as()` 문법의 쓰임새를 파악한다.

### 2.2. 학습 내용
1. **모든 체이닝의 끝, `fetch()`:** 결과 집합을 리스트(`Result`), 단일 객체(`Record`), 또는 `Optional` 등으로 뽑아내는 다양한 Fetch 전략 비교.
2. **조건절 연산자:** SQL의 `=, in, like` 연산자가 jOOQ의 메서드(`eq, in, like`)로 어떻게 일대일 매핑되는지 예제와 함께 시연.
3. **AND / OR 조합:** 여러 `Condition` 객체를 `and()`, `or()`로 묶어 복합 검색 조건을 구성하는 구조 이해.
4. **시각화 (Mermaid):**
   * DSL 조립부터 실제 쿼리 실행(`fetch`) 타임까지의 "Builder -> Execution" 라이프사이클 흐름도.

### 2.3. 기술 선택 및 개발환경 구성
* **언어:** Java 17+, Kotlin 1.9+
* **프레임워크:** Spring Boot 3.x (spring-boot-starter-jooq)
* **테스트 DB:** PostgreSQL 15 (Docker)
* **사용 클래스:** `DSLContext`, `Jooq generated Table objects (ex. USERS)`

### 2.4. 실제 개발 및 테스트 단계 (강의 시연 항목)
1. `users` 테이블에서 전체 검색: `selectFrom(USERS).fetch()` 시연
2. `where` 조건 검색: `where(USERS.ID.eq(1L))` 결과 검증
3. 복합 조건: `where(USERS.NAME.like("A%").and(USERS.AGE.gt(20)))` 시연
4. **목표:** 작성된 모든 조회가 단위 테스트 코드 상에서 실행되고 통과해야 함. (테스트 후 자동 롤백 적용)
