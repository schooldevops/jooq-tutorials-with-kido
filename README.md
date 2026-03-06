# **Java & Kotlin 동시 정복: jOOQ 마스터 클래스**


## 📅 [Phase 1] 기초 과정 (10강): jOOQ의 핵심 원리와 기본 문법

기초 과정에서는 jOOQ의 동작 메커니즘을 이해하고, SQL을 코드로 완벽하게 옮기는 법을 배웁니다.

| 강의 | 주제 | 상세 내용 (Java & Kotlin 공통 학습) |
| --- | --- | --- |
| **01** | **Why jOOQ?** | JPA, MyBatis와의 비교 분석 및 jOOQ의 Type-safety 철학 이해 |
| **02** | **환경 구축** | Docker를 활용한 DB 구성, Gradle/Maven jOOQ 플러그인 설정 |
| **03** | **코드 생성(Generation)** | DB 스키마로부터 Java/Kotlin 클래스 자동 생성 및 자동화 전략 |
| **04** | **DSLContext 설정** | Spring Boot 연동 및 `DSLContext` 빈 설정, SQL Dialect 이해 |
| **05** | **Select: 조회** | `select()`, `from()`, `where()` 기본 조회 및 별칭(Alias) 활용 |
| **06** | **Insert: 삽입** | DSL 스타일 삽입과 `UpdatableRecord`를 활용한 데이터 추가 |
| **07** | **Update & Delete** | 조건부 수정과 삭제, 그리고 안정적인 물리/논리 삭제 구현 |
| **08** | **Record와 POJO** | `fetchInto()`, `Map` 처리, 자동 생성된 POJO 클래스 활용법 |
| **09** | **정렬과 페이징** | `orderBy()`, `limit()`, `offset()`을 활용한 표준 페이징 구현 |
| **10** | **기초 프로젝트** | Java와 Kotlin으로 각각 구현하는 회원 정보 CRUD 관리자 페이지 |

---

## 🚀 [Phase 2] 중/고급 과정 (10강): 복잡한 로직과 아키텍처 최적화

실무에서 만나는 복잡한 쿼리 요구사항을 해결하고, 대규모 시스템에 적합한 구조를 설계합니다.

### **11. 동적 SQL의 마법**

* **Java:** `Condition` 객체 조립 및 `DSL.noCondition()` 활용.
* **Kotlin:** 코틀린의 `let`, `run` 및 확장 함수를 이용한 더 우아한 조건절 작성.

### **12. 다중 테이블 조인 (Joins)**

* Inner, Left, Right Join 및 `on()` 조건의 상세 제어.
* Self-join 상황에서의 테이블 별칭(Alias) 중복 해결.

### **13. 서브쿼리와 CTE (Common Table Expressions)**

* `with()` 절을 활용한 가독성 높은 복잡 쿼리 작성.
* Scala Subquery와 Correlated Subquery 처리.

### **14. 윈도우 함수 및 분석용 SQL**

* `over()`, `partitionBy()`, `rank()` 등 현대적 SQL 기능 활용.
* 대량 통계 데이터를 위한 집계 쿼리 최적화.

### **15. 커스텀 데이터 타입과 컨버터**

* Enum 매핑, JSON/JSONB 타입 처리(PostgreSQL/MySQL).
* `Converter`와 `Binding` 인터페이스를 통한 도메인 타입 변환.

### **16. 트랜잭션과 스프링 통합**

* `@Transactional` 하에서의 jOOQ 동작 원리.
* Multiple DataSource 환경에서의 jOOQ 설정 관리.

### **17. 배치(Batch) 처리와 성능**

* `batchInsert()`, `batchUpdate()`를 통한 대량 데이터 처리 성능 향상.
* 실행 계획(Explain Plan) 확인 및 인덱스 힌트 활용.

### **18. Repository 패턴 아키텍처**

* Interface-driven 설계: JPA와 jOOQ를 혼용하는 하이브리드 전략.
* QueryDSL과 비교했을 때의 아키텍처적 우위 점검.

### **19. 멀티테넌시(Multi-tenancy)**

* Schema/Table 단위의 멀티테넌시를 jOOQ `RenderMapping`으로 해결하기.

### **20. 실행 라이프사이클과 인터셉터**

* `ExecuteListener`를 활용한 SQL 로깅, 실행 시간 측정, 보안 감사(Audit).

---

## 🛠 [Phase 3] 실전 샘플 프로젝트 (5개): Baekido님을 위한 포트폴리오

학습한 내용을 바탕으로 실제 비즈니스 시나리오를 해결합니다.

1. **[회원/권한 시스템]**
* **핵심:** RBAC(Role Based Access Control) 기반의 복잡한 권한 조회 쿼리 구현.


2. **[주문/재고 관리 시스템]**
* **핵심:** 재고 차감 시 동시성 제어(`selectForUpdate`) 및 트랜잭션 보장.


3. **[실시간 대시보드 API]**
* **핵심:** 윈도우 함수를 활용하여 전일 대비 매출 변동률 및 순위 산출 API.


4. **[동적 필터링 검색 엔진]**
* **핵심:** 10개 이상의 선택적 파라미터를 가진 고성능 상품 검색(DSL 빌더 패턴).


5. **[레거시 DB 마이그레이션 툴]**
* **핵심:** 이기종 DB 간 데이터 이동 및 jOOQ를 이용한 타입 안전한 데이터 보정.
