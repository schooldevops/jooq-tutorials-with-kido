# Chapter 13: 서브쿼리와 CTE - 실습 개발 플랜

## 1. 구현 메서드

| 메서드 | 기법 | 반환 타입 |
|--------|------|---------|
| `findBooksAboveAvgYear()` | Scalar Subquery | `List<Book>` |
| `findAuthorsWithBookCount()` | CTE + GROUP BY | `List<AuthorBookCount>` |
| `findRecentBooksPerAuthor()` | CTE + JOIN | `List<RecentBook>` |

## 2. DTO

- `AuthorBookCount(id, firstName, lastName, cnt)` - 저자별 책 수
- `RecentBook(id, title, authorId, publishedYear, firstName, lastName)` - 저자 최신 책

## 3. BDD 테스트 시나리오

```
1. findBooksAboveAvgYear: 결과 책들의 연도가 모두 전체 평균보다 높음
2. findBooksAboveAvgYear: 평균 이하 책은 결과에 없음
3. findAuthorsWithBookCount: 결과의 cnt >= 1
4. findAuthorsWithBookCount: 결과 cnt 합계 = 전체 book 수
5. findRecentBooksPerAuthor: 저자별 1건씩 (각 저자의 최신 책만)
```

## 4. 실행 체크리스트
1. Java: `./gradlew test`
2. Kotlin: `./gradlew test`
3. 모든 시나리오 GREEN 확인
