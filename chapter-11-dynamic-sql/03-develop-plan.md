# Chapter 11: 동적 SQL - 실습 개발 플랜

## 1. 실습 목적
* `Condition` 리스트 조립과 `DSL.noCondition()` 방식의 동적 SQL을 구현하고, 5가지 검색 시나리오로 검증합니다.
* Kotlin은 `takeIf/let`, `fold()` 패턴으로 더 선언적인 코드를 작성합니다.

## 2. 테스트 데이터
`@Sql("/test-data.sql")` 로 다양한 title, authorId, year 조합을 삽입합니다.

## 3. 개발 플랜

### Java - DynamicSqlRepository

| 메서드 | 설명 |
|---|---|
| `searchBooks(title, authorId, year)` | Condition 리스트 방식 |
| `searchBooksWithNoCondition(title, year)` | `DSL.noCondition()` 체이닝 |

**단위 테스트 시나리오:**
```
1. 모든 파라미터 null → 전체 조회 (2건 이상)
2. title="Hamlet" → LIKE 검색, 결과에 "Hamlet" 포함
3. authorId=1 → 해당 저자 책만 반환
4. year=1600 → 특정 연도 책 반환
5. title+year 복합 → 두 조건 모두 만족하는 결과
```

### Kotlin - DynamicSqlRepository

동일한 2개 메서드, `takeIf/let` + `fold()` 스타일로 작성

**동일한 5가지 테스트 시나리오 적용**

## 4. 실행 체크리스트

1. Java: `./gradlew test`
2. Kotlin: `./gradlew test`
3. 모든 시나리오 GREEN 확인
