# Chapter 04: DSLContext 설정 - 실습 개발 플랜

## 1. 실습 목적
* Spring Boot Auto-configuration이 주입해 주는 `DSLContext`를 서비스/레포지토리 계층에서 직접 받아 사용하는 기본 골격을 완성합니다.
* `application.yml` (또는 `properties`) 상에서 `SQLDialect` 속성을 변경했을 때, jOOQ가 실제로 번역해 내는 SQL(문자열)이 DB 벤더 별로 어떻게 달라지는지 증명합니다.

## 2. 개발 플랜 (분할 정복 기반 테스트)

### Step 1: 프로젝트 복사 및 기초 설정
* Java(`codebase/java-spring`), Kotlin(`codebase/kotlin-spring`) 각각을 `chapter-04-dslcontext/code` 경로 하위에 복사.

### Step 2: Java 환경 DSLContext 구동 및 Dialect 시연 작성
* **기본 주입 시연:**  `JooqHealthCheckService` 같은 심플한 클래스를 만들고 `@RequiredArgsConstructor` (또는 수동 생성자)를 통해 `DSLContext` 주입받아 `selectOne().fetch()` 호출 증명 구조 작성.
* **Dialect 비교 테스트 (BDD):**
  * DB 커넥션을 맺지 않더라도, `DSLContext` 자체를 팩토리로 띄울 수 있는 특성을 활용하여 단위 테스트 작성.
  * *Test Case 1:* `DSL.using(SQLDialect.POSTGRES)` 로 만든 컨텍스트에서 `.limit(10)` 호출 시 -> SQL 구문에 `LIMIT` 포함 확인.
  * *Test Case 2:* `DSL.using(SQLDialect.ORACLE)` 로 만든 컨텍스트에서 동일 객체와 호출 시 -> SQL 구문에 `ROWNUM` 이 래핑되는지 검증(Assert)하여 강력함 증명.

### Step 3: Kotlin 환경 DSLContext 구동 시연
* **Kotlin 주입 시연:** `val dsl: DSLContext` 를 Spring `@Service` 생성자에서 주입받는 자연스러운 클래스 구조 구현.
* **Dialect 비교 테스트 (BDD):** Java 환경과 동일한 TDD 논리 구성 (Postgres vs Oracle 쿼리 번역 차이점) 적용.

---
본 플랜의 작성이 완료되었으므로, EXECUTION 모드로 진입하여 파일 복사부터 실제 `Dialect` 변화에 대한 흥미로운 단위 테스트 코드들을 직접 구현해 보겠습니다.
