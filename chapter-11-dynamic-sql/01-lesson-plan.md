# Chapter 11: 동적 SQL의 마법 레벨 2 - 런타임 조건 조립

## 1. 커리큘럼 분석
* **제목:** 동적 SQL의 마법 - `Condition` 객체 조립 및 `DSL.noCondition()` 활용
* **분석 내용:**
  * 실무 검색 API에서 사용자가 입력한 조건만 WHERE 절에 반영해야 합니다.
  * jOOQ의 `Condition` 인터페이스는 SQL 조건절을 타입 안전한 객체로 표현하며, 런타임에 조립 가능합니다.
  * Java는 리스트 조립 + `DSL.noCondition()`, Kotlin은 `let/takeIf` 로 더욱 우아하게 표현합니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **동적 WHERE 이해:** 정적 SQL과 달리 파라미터 존재 여부에 따라 WHERE 절이 달라지는 동적 SQL의 필요성을 이해한다.
2. **Condition 조립 패턴:** null이 아닌 파라미터만 `Condition` 리스트에 추가해 `and()` 로 조립하는 Java 패턴을 체득한다.
3. **`noCondition()` 활용:** `DSL.noCondition()`으로 시작해 `and()` 체이닝하는 깔끔한 방식을 익힌다.
4. **Kotlin 확장 함수 패턴:** `takeIf { it.isNotBlank() }?.let { BOOK.TITLE.containsIgnoreCase(it) }` 으로 null-safe 조건 조립을 마스터한다.

### 2.2. 학습 내용
1. **정적 SQL의 한계:** 조건이 없을 때 `WHERE TRUE` 또는 모든 결과가 나오는 문제
2. **Java - Condition 리스트 조립:** `ArrayList<Condition>` + `DSL.and(conditions)` 패턴
3. **Java - `noCondition()` 방식:** `var condition = DSL.noCondition(); if (title != null) condition = condition.and(...)` 패턴
4. **Kotlin - `takeIf/let` 패턴:** 함수형 스타일로 조건을 누산
5. **Mermaid 시각화:** 동적 조건 조립 플로우

### 2.3. 기술 선택 및 개발환경
* **도메인:** `book` + `author` 테이블 (searchBooks: title, authorId, year 3가지 선택적 파라미터)
* **테스트:** `@Sql("/test-data.sql")` 추가 데이터로 다양한 조건 검증

### 2.4. 구현 시나리오
1. 모든 파라미터 null → 전체 조회
2. title만 입력 → 제목 LIKE 검색
3. authorId만 입력 → 특정 저자 필터
4. year만 입력 → 출판연도 필터
5. 복합 조건 → 여러 파라미터 동시 적용
