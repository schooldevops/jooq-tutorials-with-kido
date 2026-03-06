# Chapter 02: 환경 구축 - 실습 개발 플랜

이 문서는 `02.develop-code-skill.md`의 지침에 따라 Chapter 02 강의에 포함된 실습 및 인프라 구성을 진행하기 위한 계획서입니다.

## 1. 실습 목적
* Docker Compose를 활용하여 로컬 PostgreSQL 구동 및 기초 테이블 생성.
* Spring Boot (Java / Kotlin 각각) 환경에서 `nu.studer.jooq` Gradle 플러그인을 설정하여 Database 스키마에 기반한 `Q-Class(Generated Code)`를 성공적으로 추출.

## 2. 개발 플랜 (분할 정복 및 실행 파이프라인)

### Step 1: Docker Compose 작성 및 DB 프로비저닝 (공통)
* `chapter-02-environment/docker-compose.yml` 작성.
* (중요!) 컨테이너가 뜰 때 `schema.sql`이 즉시 실행되어 기본 테이블(예: `authors`, `books`)이 적재될 수 있도록 `init-scripts` 또는 로컬 볼륨 매핑 설정.

### Step 2: Java 프로젝트 실습 구성 (`code-java`)
* `codebase/java-spring` 복사.
* `build.gradle` 파일 편집:
  * `nu.studer.jooq` 플러그인 (통상 버전 8.2) 적용.
  * DB 접속 정보(url, user, password) 및 패키지 위치(`com.example.jooq`)를 지정하는 `jooq { ... }` 제너레이터 설정 작성.
* `jooqGenerator` 의존성 블록에 PostgreSQL JDBC 드라이버 추가.
* Gradle의 `generateJooq` 태스크 실행 후 코드가 생성되는지 검증(Test).

### Step 3: Kotlin 프로젝트 실습 구성 (`code-kotlin`)
* `codebase/kotlin-spring` 복사.
* `build.gradle.kts` (또는 `build.gradle`) 편집:
  * Kotlin Generator(`KotlinGenerator`)를 사용하도록 변경하여, Java 클래스가 아닌 Kotlin Immutable 데이터 클래스 및 Object 기반 레코드가 생성되도록 세팅.
* Gradle 태스크를 실행하여 Kotlin 코드가 정상 추출되는지 확인(Test).

---

본 플랜이 기획 완료되었으므로, 다음 단계인 **실행(EXECUTION)** 모드로 전환하여 Docker 컨테이너 실행 및 빌드 스크립트 수정을 진행하겠습니다.
