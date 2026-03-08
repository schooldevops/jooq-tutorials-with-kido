# Chapter 14: 윈도우 함수 - 실습 개발 플랜

## 구현 메서드

| 메서드 | 윈도우 함수 | DTO |
|--------|-----------|-----|
| `rankBooksByYearPerAuthor()` | RANK() OVER (PARTITION BY) | `BookRank` |
| `runningTotalByYear()` | SUM() OVER (ORDER BY) | `YearlyRunningTotal` |
| `findTopRankedBookPerAuthor()` | RANK() + CTE WHERE rank=1 | `BookRank` |

## BDD 테스트 시나리오
```
1. rankBooks: 결과의 rank_in_author >= 1
2. rankBooks: 저자별 첫 번째 RANK는 1
3. runningTotal: 결과의 running_total이 누적증가
4. findTopRanked: 결과 모두 rank_in_author == 1
5. findTopRanked: 저자별로 최대 1건
```
