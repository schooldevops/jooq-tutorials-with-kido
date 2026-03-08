# 실습 개발 계획

## [요구사항] 쿼리 실행 시간 로깅 인터셉터 적용

1. **인터셉터 클래스 작성 (`PerformanceListener`)**
    - `DefaultExecuteListener` 상속.
    - `executeStart`에서 `System.nanoTime()` 기록.
    - `executeEnd`에서 소요 시간을 로깅(`System.out.println`) 및 해당 `Context`의 SQL문 추출.
    - (선택) 특정 임계 시간(예: 100ms) 초과 시 "SLOW QUERY" 문자열을 함께 남김.

2. **설정 등록 (`JooqConfig`)**
    - 작성한 `PerformanceListener`를 jOOQ Configuration 빈에 공급하도록 설정.

3. **Repository 작성 (`LifecycleRepository`)**
    - 단순 `Author` 1건 저장(`insert`), 여러 건 조회(`select`), `pg_sleep` 함수를 이용한(또는 느린) 인위적 Slow Query 트리거. (PostgreSQL의 경우 `select pg_sleep(1)`)

4. **Test 작성 (`PerformanceListenerTest`)**
    - Repository의 메서드들을 실행.
    - 애플리케이션 로그 스트림 상에 "Query Execution Time:" 이 출력되는지를 눈으로(또는 System.out 가로채기 방식 등으로) 증명. (단순 통합테스트이므로 에러 발생하지 않음을 증명)

## 프로젝트 구조

```
chapter-20-execution-interceptor/
├── 01-lesson-plan.md
├── 02-lesson-content.md
├── 03-develop-plan.md
├── code-java/
│   ├── build.gradle
│   └── src/main/java/com/example/demo/
│       ├── config/
│       │   ├── PerformanceListener.java
│       │   └── JooqConfig.java
│       └── repository/
│           └── LifecycleRepository.java
└── code-kotlin/
    ├── build.gradle
    └── src/main/kotlin/com/example/demo/
        ├── config/
        │   ├── PerformanceListener.kt
        │   └── JooqConfig.kt
        └── repository/
            └── LifecycleRepository.kt
```
