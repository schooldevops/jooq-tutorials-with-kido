# Chapter 12: 다중 테이블 JOIN - 실습 개발 플랜

## 1. 실습 목적
`book` + `author` 테이블 JOIN 3가지 패턴을 구현하고 검증합니다.

## 2. 결과 DTO: `BookWithAuthor`
- `id`, `title`, `publishedYear`, `firstName`, `lastName` 포함

## 3. 구현 메서드

| 메서드 | JOIN | 설명 |
|--------|------|------|
| `findBooksWithAuthor()` | INNER | 저자 있는 책만 |
| `findAllBooksWithAuthor()` | LEFT | 저자 없는 책도 포함 |
| `findBooksAfterYearWithAuthor(year)` | INNER + WHERE | 연도 필터 추가 |

## 4. BDD 테스트 시나리오

```
1. INNER JOIN: 결과 건수가 전체 book 수 이하
2. INNER JOIN: 모든 결과의 firstName이 not null
3. LEFT JOIN: 결과 건수 >= INNER JOIN 결과 건수
4. WHERE 필터 JOIN: 특정 연도 이후 책만 포함 검증
5. INNER JOIN 정렬: title ASC 순서 검증
```

## 5. 실행 체크리스트
1. Java: `./gradlew test`
2. Kotlin: `./gradlew test`
3. 모든 시나리오 GREEN 확인
