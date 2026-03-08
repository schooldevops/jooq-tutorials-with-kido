# Chapter 16: 트랜잭션과 스프링 통합

## 1. 학습 목표
1. **스프링과 jOOQ의 트랜잭션 통합 원리 이해:** `@Transactional` 작동 방식
2. **트랜잭션 전파 및 롤백 검증:** 서비스 계층에서 예외 발생 시 DB에 반영되지 않는 것을 확인
3. **분리된 계층 설계:** DB I/O를 담당하는 Repository와 비즈니스/트랜잭션을 관장하는 Service 역할 분리

## 2. 핵심 개념
- **SpringTransactionProvider:** Spring Boot의 jOOQ 스타터는 기본적으로 `DataSourceTransactionManager`와 연동되는 Provider를 주입해 줍니다. 
- 개발자는 jOOQ 전용 트랜잭션 API 대신, 익숙한 Spring의 `@Transactional` 어노테이션을 그대로 사용할 수 있습니다.

## 3. 실습 시나리오
- `TransactionRepository`: jOOQ `dsl.insertInto()...`
- `TransactionService`:
  1. 정상 로직 (Insert 성공)
  2. 예외 발생 로직 (Insert 후 RuntimeException -> Rollback)
