# Chapter 05: Select (기본 조회와 조건 제어) - 실습 개발 플랜

## 1. 실습 목적
* `DSLContext`와 자동 생성된 jOOQ Table 객체들을 활용하여 컴파일 타임에 검증되는 Type-Safe 검색 쿼리를 작성합니다.
* 다양한 조건 조합(AND/OR, Like 연산 등)을 적용해 SQL 작성 경험과 동일한 Fluent API의 편의성을 테스트 코드로 증명합니다.

## 2. 사전 준비 (환경 구성)
> **중요:** 본 챕터부터는 실제 DB 연동이 필요합니다. 실습 전 Chapter 02에서 만든 docker-compose.yml이 구동 중이어야 하며, jOOQ 코드가 정상적으로 추출(Generation)되어 있어야 합니다. (이번 자동화 봇 실행 단계에서 이를 검증 및 수행합니다.)

### Step 1: 기본 프로젝트 복사 및 설정 빌드업
* `codebase/java-spring`과 `codebase/kotlin-spring`을 `chapter-05-select` 하위로 각각 복사합니다.
* **플러그인 옵션 추가 (`build.gradle`):**
  * `nu.studer.jooq` 9.0 버전을 적용하고, 컴파일 전 jOOQ 생성기가 먼저 동작하도록 의존성(`generateJooq`) 설정 혹은 코드 생성 태스크를 활성화합니다.
  * DB 접근 정보: `localhost:5432/jooq_demo`, user=postgres, pw=postgres

## 3. 개발 플랜 (분할 정복 기반 테스트 시나리오)

### Step 2: Java 환경 - JooqSelectService 및 단위 테스트 작성
* **`JooqSelectService.java` 구성:**
  * `DSLContext`를 자동 주입받는 Service 객체 생성.
  * `findUsersAll()`: 전체 `UserRecord` 반환 메서드 (selectFrom)
  * `findUsersByNameAndAge(String namePrefix, Integer minAge)`: 다중 `Where` 조건(`getName().like()`, `getAge().gt()`) 적용 메서드 구현.
* **단위 테스트 (`SelectQueryTest.java`):**
  * 실제 DB와 연결된 `@SpringBootTest` 구동 (또는 `@jooqTest` 사용 가능).
  * 쿼리 검증: 반환된 결과 사이즈나 데이터 무결성을 검증하고, 쿼리가 성공적으로 동작함을 확인합니다.

### Step 3: Kotlin 환경 - JooqSelectService 및 단위 테스트 작성
* **`JooqSelectService.kt` 구성:**
  * Kotlin 문법(`class JooqSelectService(private val dsl: DSLContext)`)의 이점을 살린 깔끔한 쿼리 서비스 레이어 구현.
  * jOOQ의 코틀린 확장 모듈을 엮어 조건식을 더 코틀린스럽게 작성. (선택적)
* **단위 테스트 (`SelectQueryTest.kt`):**
  * Java와 동일하게 Spring Boot 환경을 올려 SQL 로그 및 Fetch 성공 여부를 검증합니다.

---
본 플랜 작성 완료 후, EXECUTION 모드로 전환하여:
1. 로컬 Docker 컨테이너 실행 (`docker-compose up -d`)
2. jOOQ 코드 제너레이터 실행 (`./gradlew generateJooq`)
3. 본격적인 Service 및 Test 코드 작성을 연이어 수행하겠습니다. (테스트가 확인된 이후 Docker 프로세스를 종료합니다.)
