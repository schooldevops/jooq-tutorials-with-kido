# Chapter 16: 트랜잭션 - 실습 개발 플랜

## 구현 클래스

1. `TransactionRepository`
   - `saveAuthor(int id, String first, String last)`
   - `findById(int id)` (검증용)

2. `TransactionService` (`@Transactional` 적용)
   - `saveAuthorAndBookSuccessfully(id)`
   - `saveAuthorAndThrowException(id)`

## BDD 테스트 시나리오 (`TransactionTest`)
```
1. saveAuthorAndBookSuccessfully:
   - when: 정상 메서드 호출
   - then: findById(id) 로 조회 시 데이터가 존재해야 한다. (Commit)

2. saveAuthorAndThrowException:
   - when: 예외 발생 메서드 호출 (assertThrows)
   - then: findById(id) 로 조회 시 데이터가 존재하지 않아야 한다. (Rollback)
```
