# Chapter 18: 고급 아키텍처 - JPA와 jOOQ 함께 사용하기

18강에서는 대규모 백엔드 서비스에서 가장 널리 사용되는 **[JPA (CUD) + jOOQ (Read)] CQRS 하이브리드 아키텍처**를 실습합니다.

---

## 1. CQRS의 개념적 적용

가장 실용적인 아키텍처 배분 방식은 다음과 같습니다:

1. **상태 변경 (Command: Insert, Update, Delete)**
    - 비즈니스 룰이 복잡하고 무결성이 중요한 구간.
    - **JPA(Hibernate)** 를 사용해 객체의 생명주기 관리를 위임하고, 더티 체킹을 통한 Update를 활용합니다.
2. **데이터 조회 (Query: Select)**
    - 다양한 화면 요구사항에 맞춘 복잡한 JOIN과 DTO 반환이 필요한 구간.
    - **jOOQ** 를 사용해 N+1 문제 없이 최적화된 SQL을 직접 제어하고 성능을 극대화합니다.

---

## 2. 주의할 점: 트랜잭션과 1차 캐시의 타이밍 (중요 ★)

Spring의 `@Transactional` 은 JPA와 jOOQ의 트랜잭션을 완벽하게 공유해줍니다. 즉, 하나의 트랜잭션 매니저에서 관리가 됩니다.
하지만! **JPA의 내부 동작 원리(Write-Behind, 쓰기 지연)** 때문에 치명적인 논리 오류가 발생할 수 있습니다.

### 논리적인 오류 (Read Uncommitted 현상)
```java
@Transactional
public void saveAndFind(Author a) {
    // 1. JPA로 영속화 (Insert 쿼리 안 나감! 메모리(1차 캐시)에만 존재)
    jpaRepository.save(entity);

    // 2. jOOQ로 조회 (실제 DB로 Select 쿼리 날림)
    AuthorDto dto = jooqRepository.findById(a.getId());
    
    // dto == null !!! (jOOQ는 메모리에 있는 걸 모름)
}
```

### 해결책: 강제 동기화 (`flush`)
jOOQ로 쿼리를 날리기 전에, **메모리(JPA 1차 캐시)의 변경사항을 억지로 DB까지 내려보내야(Flush)** jOOQ가 정상적으로 읽을 수 있습니다.

```java
@Transactional
public void saveAndFind(Author a) {
    // 1. JPA로 영속화 후 Flush (Insert 쿼리 즉시 발생)
    jpaRepository.saveAndFlush(entity); 
    // 혹은 jpaRepository.flush();

    // 2. jOOQ로 조회 (DB에 데이터가 도달했으므로 정상 조회됨!)
    AuthorDto dto = jooqRepository.findById(a.getId());
}
```

---

기억하세요! **상태 변경은 JPA, 복잡한 조회는 jOOQ!** 그리고 **jOOQ 조회 직전엔 JPA Flush!**
이 원칙만 지키면 최고 성능의 안정적인 애플리케이션을 만들 수 있습니다.
