# Chapter 13: 서브쿼리와 CTE 레벨 2

## 1. 커리큘럼 분석
* **제목:** 서브쿼리와 CTE (Common Table Expressions)
* **핵심 내용:**
  * `with()` 절로 가독성 높은 복잡 쿼리 작성
  * Scalar Subquery: 단일 값을 반환하는 서브쿼리
  * Correlated Subquery: 외부 쿼리를 참조하는 서브쿼리
  * CTE: 임시 결과셋을 이름 붙여 가독성 확보

## 2. 학습 목표
1. **Scalar Subquery:** `DSL.select().from().asField()` 패턴으로 단일 값 서브쿼리를 WHERE 조건에 활용
2. **CTE (`with()`):** `dsl.with("cteName").as(subquery)` 로 읽기 쉬운 복잡 쿼리 작성
3. **CTE + JOIN:** CTE에서 추출한 결과를 메인 쿼리에서 JOIN으로 결합

## 3. 구현 시나리오
- **Scalar Subquery:** 전체 book의 평균 published_year보다 이후에 출판된 책 목록 조회
- **CTE:** 각 저자별 책 수를 집계하는 CTE → `AuthorBookCount` DTO 반환
- **CTE + JOIN:** 저자별 최신 출판연도를 CTE로 구한 뒤 → 그 최신 책 상세 정보를 JOIN
