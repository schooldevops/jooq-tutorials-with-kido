# Chapter 01: Why jOOQ? - 실습 개발 플랜

이 문서는 `02.develop-code-skill.md`의 지침에 따라 Chapter 01 강의에 포함된 실습 코드를 작성하기 위한 계획서입니다.

## 1. 실습 목적
* **Type-safety 시연:** MyBatis 기반 쿼리가 발생시키는 런타임 에러와 jOOQ가 방지해 주는 컴파일 타임 에러(빌드 실패)를 직접 비교하여 시연합니다.

## 2. 개발 플랜 (분할 정복 및 BDD)

### Step 1: 개발 환경 준비 (코드베이스 복사)
* `codebase/java-spring` (또는 `kotlin-spring`)을 `chapter-01-why-jooq/code` 디렉토리로 복사.
* 복사된 프로젝트의 구동 확인 및 기본적인 패키지 구조 세팅.

### Step 2: Database Schema 및 초기 설정
* 간단한 `users` 테이블 구조(DDL) 및 초기 데이터(DML) 스크립트 작성 (`src/main/resources/schema.sql`, `data.sql`).

### Step 3: MyBatis 적용 및 런타임 에러 시연 시나리오
* **목표:** 개발자가 오타를 내었을 때 시스템이 에러를 잡지 못하고 런타임에 죽는 상황 재현.
* **BDD/TDD:**
  * `UserMapper` 인터페이스와 XML 매퍼 작성.
  * **일부러 컬럼명 오타**를 내어 작성.
  * 테스트 코드에서 해당 메서드를 호출하면, 컴파일은 통과하지만 실행 시 `BadSqlGrammarException`이 발생하는 것을 확인(단언).

### Step 4: jOOQ 적용 및 컴파일 타임 오류 (Type-safety) 체험 시나리오
* **목표:** jOOQ 환경에서는 동일한 실수를 저지르면 아예 컴파일(빌드)조차 안 된다는 점 시연.
* **BDD/TDD:**
  * jOOQ Generator를 통해 `users` 테이블 정보를 Java/Kotlin 클래스로 생성 방안 제시 (이번 장에서는 제너레이터 없이도 Type-safety의 개념만 보여줄 수 있게 미리 생성된 모델을 모의로 쓰거나 제너레이션 생략 가능, Chapter 02/03에서 정식으로 다룸).
  * **시연:** 생성된 QClass/Table 클래스에 정의되지 않은 필드(오타)를 코드 단에 적었을 때, Java/Kotlin 컴파일러가 잡아내는 현상을 소스코드 주석/설명형태로 확인. 

본 플랜 승인 후, Step 1의 기본 코드베이스 복사를 진행하고 코드를 구현하겠습니다.
