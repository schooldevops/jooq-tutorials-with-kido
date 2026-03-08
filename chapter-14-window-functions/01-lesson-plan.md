# Chapter 14: 윈도우 함수 및 분석용 SQL

## 1. 학습 목표
1. **RANK() OVER (PARTITION BY):** 파티션(그룹) 내 순위 계산
2. **SUM() OVER (ORDER BY):** 누적 합계 (Running Total)
3. **윈도우 함수 + CTE:** RANK 결과를 CTE로 감싸 1위만 필터링

## 2. 핵심 jOOQ API
- `DSL.rank().over(...)` - 윈도우 함수 시작
- `.partitionBy(field)` - 그룹 분할
- `.orderBy(field)` - 그룹 내 정렬
- `DSL.sum(field).over().orderBy(field)` - 누적합

## 3. 시나리오
- 저자별 출판연도 기준 책 순위 계산 (RANK)
- 연도별 출판 책 수 누적합 (SUM OVER)
- 저자별 1위(최신) 책만 추출 (RANK+CTE)
