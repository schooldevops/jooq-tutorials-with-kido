# Chapter 02: 환경 구축 (Docker 기반 DB 구성 및 jOOQ 플러그인 설정) - 강의 계획서

## 1. 컬리큘럼 제목 분석
* **제목:** 환경 구축 (Docker를 활용한 DB 구성, Gradle jOOQ 플러그인 설정)
* **분석 내용:**
  * jOOQ는 Database-First 접근 방식을 사용하므로 코드를 작성하기 전에 반드시 살아있는 데이터베이스 스키마(또는 DDL)가 존재해야 합니다.
  * 로컬 환경에서 가장 쉽게 DB를 구성하는 방법인 Docker Compose를 활용하여 PostgreSQL을 띄웁니다.
  * Spring Boot 프로젝트에서 Gradle의 `nu.studer.jooq` 플러그인을 활용하여, 스키마 정보를 읽어온 뒤 Java/Kotlin 코드를 자동 생성(Code Generation)하는 파이프라인을 구축합니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **Database-First 접근법 이해:** 왜 실제 DB를 먼저 띄워야만 jOOQ가 제 몫을 다하는지 이해한다.
2. **도커 컴포즈(Docker Compose) 활용:** 실무와 동일한 구성의 PostgreSQL 환경을 로컬에 구축한다.
3. **Gradle 플러그인 연동 및 코드 생성:** 빌드 스크립트(`build.gradle`)에 jOOQ Generator를 설정하고, `generateJooq` 태스크를 통해 Q-Class들이 자동으로 만들어지는 과정을 습득한다.

### 2.2. 학습 내용
1. **도입:** ORM(JPA)의 Code-First(Entity 기반 스키마 자동 생성)와 jOOQ의 Database-First 패러다임 차이 설명.
2. **PostgreSQL 환경 구축:** `docker-compose.yml` 리소스 작성 및 실행 방법 (`docker-compose up -d`).
3. **스키마 및 데이터 초기화 전략:** Flyway 등을 쓰는 것이 정석이나, 본 강의의 직관성을 위해 Spring Boot의 `schema.sql` 또는 도커 초기화 스크립트를 통한 깡통 테이블 생성.
4. **Gradle 세팅:** 
   * `nu.studer.jooq` 플러그인 의존성 추가.
   * jOOQ Generator 설정 블록(JDBC 커넥션 정보, 패키지 타겟 등) 작성법 상세 리뷰.
5. **확인 (Mermaid 시각화):**
   * Code Gen 빌드 파이프라인 흐름도(BPMN/Sequence)를 통해 어느 시점에 코드가 생성되는지 시각적으로 인지.

### 2.3. 기술 선택 및 개발환경 구성
* **언어/프레임워크:** Java 17+, Kotlin 1.9+, Spring Boot 3.x
* **데이터베이스:** PostgreSQL 15+ (Docker)
* **빌드 도구:** Gradle 8.x + `nu.studer.jooq` (버전 8.2+)

### 2.4. 실제 개발 및 테스트 단계 (강의 시연 항목)
1. 프로젝트 루트에 `docker-compose.yml` 파일 생성 및 `PostgreSQL` 기동.
2. 기초 테이블 (예: `authors`, `books`) DDL 생성.
3. 깡통 프로젝트의 `build.gradle` / `build.gradle.kts` 수정 및 로컬 연동 세팅.
4. `./gradlew generateJooq` 명령어 수행.
5. `target/generated-sources/jooq` 경로에 Java/Kotlin 클래스들이 폭포수처럼 쏟아져 나오는 마법(?) 시연.

---

> 본 강의 계획서에 기재된 내용을 기반으로, 다음 단계인 **강의 생성(강의 스크립트 작성 및 다이어그램 첨부)**을 자동 진행하겠습니다.
