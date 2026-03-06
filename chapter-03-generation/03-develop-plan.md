# Chapter 03: 코드 생성 고도화 - 실습 개발 플랜

## 1. 실습 목적
* Chapter 02의 기본 제너레이터 설정에서 한 걸음 나아가, **실무적인 Generator 튜닝(Includes/Excludes, POJO 생성, Immutable 설정)** 방식을 실습합니다.

## 2. 개발 플랜 (분할 정복)

### Step 1: 공통 코드베이스 복사
* Java는 `codebase/java-spring`을 `chapter-03-generation/code-java`로 복사.
* Kotlin은 `codebase/kotlin-spring`을 `chapter-03-generation/code-kotlin`으로 복사.

### Step 2: Java Generator 고도화 실습
* `build.gradle` 내의 `jooq` 블록을 정밀하게 수정.
* **Generate 옵션 추가:** 
  * `pojos = true`, `daos = true` 옵션을 켜서 단순 Table/Record 외의 객체도 추출되도록 설정.
* **Database 필터링:** 
  * `excludes = 'flyway_schema_history | system_.*'` 와 같은 정규식을 반영하여, 불필요한 메타 테이블을 제외하는 훈련 시연.
* 터미널 명령어(모의) 혹은 설정 파일 생성을 통해 해당 스크립트 기반 코드 추출이 가능함을 증명.

### Step 3: Kotlin Generator 고도화 (Immutable POJO) 실습
* Kotlin 환경의 `build.gradle.kts` (또는 `build.gradle`) 수정.
* **Generate 옵션 추가:**
  * `pojos = true` 외에, 핵심인 `immutablePojos = true`를 켜서 Kotlin의 `val` 기반 100% 불변 데이터 클래스(Data Class)가 쏟아져 나오게 세팅.
* 스키마 제외(`excludes`) 규칙은 Java와 동일하게 적용.

---
본 플랜 작성 완료 후, EXECUTION 모드로 전환하여 파일 복사 및 `build.gradle`의 jOOQ 세팅 수정을 진행하겠습니다.
