# Chapter 04: DSLContext 설정 - Spring Boot 연동 및 빈 설정, SQL Dialect 이해

## 1. 컬리큘럼 제목 분석
* **제목:** DSLContext 설정 (Spring Boot 연동 및 `DSLContext` 빈 설정, SQL Dialect 이해)
* **분석 내용:**
  * 이전 챕터들에서는 jOOQ 코드를 생성하는 "빌드 타임(Build Time)" 과정을 마쳤습니다.
  * 본 챕터부터는 드디어 애플리케이션 "런타임(Runtime)"으로 넘어옵니다.
  * Spring Boot가 jOOQ의 핵심 엔진인 `DSLContext`를 어떻게 자동으로 엮어내는지(Auto-Configuration) 파악하고, 여러 DB 종류에 맞게 SQL을 번역하는 `SQLDialect`의 역할을 학습합니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **DSLContext의 의미:** jOOQ의 시작과 끝인 `DSLContext`가 무엇이며 어떤 역할을 하는지 설명할 수 있다.
2. **Spring Boot Auto-configuration 이해:** 별도의 Bean 등록 설정 없이도 어떻게 `DSLContext`가 생성되는지(ConnectionProvider, TransactionProvider) 마법을 푼다.
3. **SQL Dialect의 중요성:** PostgreSQL, MySQL, Oracle 등 각기 다른 DB 방언을 jOOQ가 어떻게 단일 코드 안에서 추상화하여 번역해 주는지 이해한다.

### 2.2. 학습 내용
1. **DSL과 Context:** 이름에 담긴 의미 파악(`Domain Specific Language Context`).
2. **Bean 주입 방법:** `@Autowired` (혹은 생성자 주입)를 통해 Service 계층에서 `DSLContext`를 가져와 사용하는 직관적이고 단순한 방법 안내.
3. **Application Properties 설정:** `spring.jooq.sql-dialect` 속성을 통해 방언(Dialect)을 교체했을 때 쿼리 번역 양상이 어떻게 달라지는지(예: `LIMIT` vs `ROWNUM`) 설명.
4. **시각화 (Mermaid):**
   * Spring 의존성 주입(DI) 흐름 안에서 커넥션 풀(HikariCP) -> TransactionAwareDataSourceProxy -> jOOQ Configuration -> DSLContext 로 이어지는 스프링 아키텍처 다이어그램 작성.

### 2.3. 기술 선택 및 개발환경 구성
* **언어:** Java 17+, Kotlin 1.9+
* **프레임워크:** Spring Boot 3.x (spring-boot-starter-jooq)
* **주요 객체:** `org.jooq.DSLContext`, `org.jooq.SQLDialect`

### 2.4. 실제 개발 및 테스트 단계 (강의 시연 항목)
1. `CustomService` 객체를 만들고 생성자로 `DSLContext` 주입받기.
2. `dslContext.selectOne().fetch()` 같은 초간단 심박수(Health Check) 쿼리 날려보기.
3. 테스트 코드 상에서 의도적으로 Dialect 설정을 변경하여 쿼리 로깅 결과 관찰하기.

---

> 본 강의 계획서 작성 후 연계된 강의 스크립트 작성 및 실습 플랜 세팅을 바로 이어나가겠습니다.
