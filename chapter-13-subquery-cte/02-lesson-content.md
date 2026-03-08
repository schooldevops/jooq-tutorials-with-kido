# Chapter 13: 서브쿼리와 CTE (Common Table Expressions)

13강에서는 **복잡한 쿼리를 가독성 있게 분해**하는 두 가지 핵심 기법을 마스터합니다! 🧩

---

## 1. Scalar Subquery - 단일 값 서브쿼리

평균 출판연도보다 최신인 책을 찾아야 할 때, 서브쿼리를 바로 WHERE 절에 사용합니다.

```java
// Java: Scalar Subquery
public List<Book> findBooksAboveAvgYear() {
    // 서브쿼리: 전체 평균 출판연도
    var avgYear = DSL.select(DSL.avg(BOOK.PUBLISHED_YEAR))
                     .from(BOOK)
                     .asField("avg_year");

    return dsl.selectFrom(BOOK)
              .where(BOOK.PUBLISHED_YEAR.gt(avgYear))
              .orderBy(BOOK.PUBLISHED_YEAR.desc())
              .fetchInto(Book.class);
}
```

```kotlin
// Kotlin: Scalar Subquery
fun findBooksAboveAvgYear(): List<Book> {
    val avgYear = DSL.select(DSL.avg(BOOK.PUBLISHED_YEAR))
                     .from(BOOK)
                     .asField<BigDecimal>("avg_year")

    return dsl.selectFrom(BOOK)
        .where(BOOK.PUBLISHED_YEAR.gt(avgYear))
        .orderBy(BOOK.PUBLISHED_YEAR.desc())
        .fetchInto(Book::class.java)
}
```

> **핵심:** `DSL.select(...).from(...).asField("별칭")` 으로 서브쿼리를 필드처럼 사용합니다.

---

## 2. CTE (`with()`) - 가독성 높은 임시 결과셋

```mermaid
flowchart TD
    A["WITH book_count AS (...)"] --> B["SELECT * FROM book_count"]
    B --> C["저자별 책 수 결과"]
```

```java
// Java: CTE with()
public List<AuthorBookCount> findAuthorsWithBookCount() {
    // CTE 정의
    var bookCountCte = DSL.name("book_count").fields("author_id", "cnt")
            .as(DSL.select(BOOK.AUTHOR_ID, DSL.count().as("cnt"))
                   .from(BOOK)
                   .groupBy(BOOK.AUTHOR_ID));

    return dsl.with(bookCountCte)
              .select(
                  AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME,
                  bookCountCte.field("cnt")
              )
              .from(bookCountCte)
              .join(AUTHOR).on(AUTHOR.ID.eq(bookCountCte.field("author_id", Integer.class)))
              .orderBy(DSL.field("cnt").desc())
              .fetchInto(AuthorBookCount.class);
}
```

---

## 3. CTE + JOIN - 최신 책 조회

```java
// Java: CTE + JOIN
public List<RecentBook> findRecentBooksPerAuthor() {
    // CTE: 저자별 최신 출판연도
    var maxYearCte = DSL.name("max_year").fields("author_id", "max_y")
            .as(DSL.select(BOOK.AUTHOR_ID, DSL.max(BOOK.PUBLISHED_YEAR).as("max_y"))
                   .from(BOOK)
                   .groupBy(BOOK.AUTHOR_ID));

    return dsl.with(maxYearCte)
              .select(
                  BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_YEAR,
                  AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME
              )
              .from(BOOK)
              .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
              .join(maxYearCte)
                  .on(BOOK.AUTHOR_ID.eq(maxYearCte.field("author_id", Integer.class))
                      .and(BOOK.PUBLISHED_YEAR.eq(maxYearCte.field("max_y", Integer.class))))
              .orderBy(AUTHOR.LAST_NAME.asc())
              .fetchInto(RecentBook.class);
}
```

---

## 4. 세 가지 기법 비교

| 기법 | 용도 | jOOQ 메서드 |
|------|------|------------|
| **Scalar Subquery** | WHERE 절에 단일 값 서브쿼리 | `.asField("alias")` |
| **CTE (with)** | 복잡 쿼리를 이름 붙여 분리 | `dsl.with(cte).select(...)` |
| **CTE + JOIN** | CTE 결과를 메인 쿼리와 결합 | `.join(cte).on(...)` |

---

## 5. 요약

오늘 우리는:
1. **Scalar Subquery**로 집계 결과를 동적 WHERE 조건에 활용했습니다.
2. **`with()` CTE**로 복잡한 집계 쿼리를 읽기 쉽게 분리했습니다.
3. **CTE + JOIN**으로 CTE 결과와 메인 테이블을 결합해 최신 책을 조회했습니다.

다음 14강에서는 **윈도우 함수 및 분석용 SQL**을 다룹니다!
