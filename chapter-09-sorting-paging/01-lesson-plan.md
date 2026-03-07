# Chapter 09: 정렬과 페이징 레벨 1 - orderBy, limit, offset 완전 정복

## 1. 커리큘럼 분석
* **제목:** 정렬과 페이징: `orderBy()`, `limit()`, `offset()`을 활용한 표준 페이징 구현
* **분석 내용:**
  * 실무 API에서 게시판, 상품 목록, 검색 결과 등 거의 모든 목록 조회에 정렬과 페이징이 필요합니다.
  * jOOQ는 SQL의 `ORDER BY`, `LIMIT`, `OFFSET`을 Type-Safe한 메서드로 완벽히 표현합니다.
  * 단순 정렬부터 다중 컬럼 정렬, 그리고 정렬과 페이징을 결합한 실전 패턴까지 단계적으로 익힙니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **정렬(ORDER BY) 체득:** `FIELD.asc()`, `FIELD.desc()` 로 단일/다중 컬럼 오름차순·내림차순 정렬을 작성할 수 있다.
2. **Offset 페이징 구현:** `limit(size).offset(page * size)` 조합으로 표준 페이징 쿼리를 작성할 수 있다.
3. **정렬 + 페이징 복합 적용:** 실무에서 가장 많이 쓰이는 **정렬된 페이지 목록** 쿼리를 안전하게 구현한다.

### 2.2. 학습 내용
1. **단일 컬럼 정렬:** `orderBy(BOOK.TITLE.asc())` / `orderBy(BOOK.PUBLISHED_YEAR.desc())`
2. **다중 컬럼 정렬:** `orderBy(BOOK.PUBLISHED_YEAR.desc(), BOOK.TITLE.asc())` - 1차/2차 정렬키 설정
3. **Offset 페이징:** `limit(size).offset(page * size)` — 페이지 번호(0-based)와 페이지 크기로 슬라이싱
4. **복합 패턴:** 다중 정렬 + 페이징을 한 쿼리에 결합
5. **시각화 (Mermaid):** 페이징 구조 다이어그램 + 쿼리 조립 플로우

### 2.3. 기술 선택 및 개발환경 구성
* **언어:** Java 17+, Kotlin 1.9+
* **프레임워크:** Spring Boot 3.x (spring-boot-starter-jooq)
* **테스트 DB:** PostgreSQL 15 (Docker, `jooq_demo`)
* **테스트 데이터:** `@Sql("test-data.sql")`로 추가 데이터 삽입 → 페이징 결과 검증 강화
* **테스트 전략:** `@SpringBootTest` + `@Transactional`

### 2.4. 실제 개발 및 테스트 단계 (강의 시연 항목)
1. `findBooksOrderedByTitle()`: 전체 책 목록을 제목 오름차순으로 정렬 후 반환, 첫 번째 요소가 알파벳 순서상 가장 앞인지 검증
2. `findBooksOrderedByYearDesc()`: 출판연도 내림차순으로 정렬, 최신 책이 첫 번째에 오는지 검증
3. `findBooksWithPaging(0, 2)`: 1페이지(0-based) 크기 2로 조회, 결과가 2개 이하인지 검증
4. `findBooksWithMultiSort(0, 2)`: 연도 내림차순 + 제목 오름차순 정렬 후 페이징 적용
5. **목표:** 모든 쿼리가 단위 테스트에서 통과. 롤백 적용.
