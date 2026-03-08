# Chapter 12: 다중 테이블 조인 (Joins) 레벨 2

## 1. 커리큘럼 분석
* **제목:** 다중 테이블 조인 - Inner, Left, Right Join 및 `on()` 조건의 상세 제어
* **핵심 내용:**
  * jOOQ에서 여러 테이블을 JOIN하는 방법을 이해합니다.
  * `INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN` 의 차이를 이해하고 적용합니다.
  * `on()` 조건을 사용하여 JOIN 조건을 명세적으로 제어합니다.
  * Self-join 시 테이블 별칭(`as()`)으로 충돌을 해결합니다.

## 2. 강의 계획

### 2.1. 학습 목표
1. **INNER JOIN:** `join(TABLE).on(조건)` - 양쪽 테이블 모두에 일치하는 데이터만 반환
2. **LEFT JOIN:** `leftJoin(TABLE).on(조건)` - 왼쪽 테이블의 모든 행 + 오른쪽 일치 데이터 반환
3. **WHERE 조건과의 결합:** JOIN 결과에 추가적인 WHERE 필터 적용
4. **DTO 매핑:** 복수 테이블 결과를 단일 DTO 객체에 `map()` 또는 `fetchInto()` 로 매핑

### 2.2. 학습 내용
1. **기본 INNER JOIN:** book ↔ author 연결, 저자가 없는 책은 제외
2. **LEFT JOIN:** 저자가 없는 책도 포함, null 처리
3. **WHERE 조건 추가:** JOIN 후 조건 필터 (`published_year >= year`)
4. **결과 매핑:** `fetchInto(책+저자DTO.class)` 또는 `fetch().map { ... }`

### 2.3. 기술 선택
* **DTO:** Java record (`BookWithAuthor`), Kotlin data class
* **결과 매핑:** `into(BookWithAuthor.class)` 활용 (POJO 자동 매핑)
* **테스트:** `@Sql("/test-data.sql")` + 5가지 BDD 시나리오
