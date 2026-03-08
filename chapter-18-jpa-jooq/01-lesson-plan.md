# Chapter 18: 고급 아키텍처 - JPA와 jOOQ 함께 사용하기

## 1. 학습 목표
1. **CQRS (Command and Query Responsibility Segregation) 맛보기:** 상태 관리(CUD)와 단순 조회(Read)의 관심사를 분리하는 이유와 원리 이해.
2. **JPA + jOOQ 하이브리드 구성:** 쓰기는 강력한 ORM(JPA)에 위임하고, 읽기는 강력한 SQL Builder(jOOQ)에 위임하는 실무적인 최고의 아키텍처 학습.
3. **트랜잭션 공유 유의점:** 동일한 `@Transactional` 내에서 JPA 영속성 컨텍스트(1차 캐시)의 지연 쓰기(Write-Behind) 특성 때문에 벌어지는 문제와 해결책(`flush`) 파악.

## 2. 왜 두 기술을 섞어 쓰나요?
- **JPA의 강점:** 풍부한 객체 지향 매핑, 영속성 컨텍스트를 통한 더티 체킹(Dirty Checking), 캐싱 등을 활용해 비즈니스 로직(저장, 수정)을 단순하고 안전하게 작성.
- **jOOQ의 강점:** 복잡한 JOIN, 서브쿼리, 윈도우 함수 사용 및 DTO 매핑 등 동적이고 무거운 '조회'를 타입 세이프(Type-Safe)하게, 그리고 최상의 퍼포먼스로 작성.
- **결론:** CUD(JPA) + Read(jOOQ) 조합은 현재 많은 엔터프라이즈 환경에서 표준으로 채택하는 방식입니다.

## 3. 실습 시나리오
- `AuthorEntity`와 Spring Data JPA (`AuthorJpaRepository`) 구성
- 조회 전용 `AuthorJooqRepository` 구성
- `AuthorService` 내에서 JPA로 저장하고 직후 jOOQ로 조회하는 흐름 구축 및 `HybridArchitectureTest` 작성
