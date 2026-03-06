# Chapter 03: 코드 생성(Generation) - 자동화 전략과 커스터마이징 플랜

## 1. 컬리큘럼 제목 분석
* **제목:** 코드 생성(Generation) - DB 스키마로부터 Java/Kotlin 클래스 자동 생성 및 자동화 전략
* **분석 내용:**
  * 지난 챕터에서는 로컬 DB를 띄우고 플러그인의 최소 설정을 맛보았습니다.
  * 이번 챕터에서는 한 단계 나아가, jOOQ Generator를 **우리 입맛에 맞게 튜닝(Customizing)** 하는 방법과 **도메인 자동화 전략**을 학습합니다.
  * DB의 원시 데이터 타입(예: `VARCHR(1) Y/N`)을 Java의 `enum` 혹은 `Boolean`으로 매핑하는 **Converter & Binding**의 개념을 제너레이터 레벨에서 미리 잡아두는 전략을 세웁니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **Generator 설정 고도화:** 기본 패키지나 디렉토리 지정 외에, 생성 제외 정책(`excludes`)이나 네이밍 컨벤션 전략을 이해한다.
2. **Forced Types 컴포넌트 이해:** DB 스키마와 애플리케이션 간의 데이터 불일치를 해소하는 `forcedType` 변환 로직을 설정할 수 있다.
3. **Java와 Kotlin의 생성 차이점:** POJO를 생성할 것인지, Kotlin Data Class를 생성할 것인지 세밀히 조정하는 방법을 안다.

### 2.2. 학습 내용
1. **기본 생성 클래스 탐험:** jOOQ가 만들어내는 `Table`, `Record`, `POJO` 클래스들의 역할 분류.
2. **코드 사이즈 최적화:** `include`/`exclude` 정규식을 활용해 내가 제어할 테이블만 타겟으로 만들어 불필요한 클래스 생성을 막는다.
3. **커스텀 타입 적용 전략:** DB의 Enum 타입 혹은 특정 문자열 포맷을 Java의 객체로 강제 변환(ForcedType)하는 Generator 설정 방법(이론).
4. **시각화 (Mermaid):**
   * Generator 설정 컴포넌트 내부 다이어그램 (Database, Target, Strategy, Generate 속성 구조도).

### 2.3. 기술 선택 및 개발환경 구성
* **언어/프레임워크:** Java 17+, Kotlin 1.9+, Spring Boot 3.x
* **제너레이터 설정:** `build.gradle` (Gradle 8.x)
* **테스트 DB:** PostgreSQL (의존성은 로컬 환경을 가정)

### 2.4. 실제 개발 및 테스트 단계 (강의 시연 항목)
1. 프로젝트의 `build.gradle` jooq 설정 블록을 고도화.
2. 데이터베이스 스키마에서 불필요한 시스템 테이블(예: `flyway_schema_history`) 생성 제외 로직 작성.
3. `record`, `pojo`, `daos` 등 jOOQ가 제공하는 다양한 객체 옵션을 toggle(true/false) 해보며 변화 확인.
4. Kotlin 환경에서 Immutable 옵션(`immutablePojos = true`)을 통해 Thread-safe한 코드 생성 세팅.

---

> 본 계획안 작성 후, 스크립트 본문과 다이어그램을 포함하는 강의 교재 및 실습 플랜 생성을 이어서 진행합니다.
