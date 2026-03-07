# Chapter 10: 기초 프로젝트 레벨 1 - 회원 정보 CRUD 관리자

## 1. 커리큘럼 분석
* **제목:** 기초 프로젝트 - Java와 Kotlin으로 각각 구현하는 회원 정보 CRUD 관리자 페이지
* **분석 내용:**
  * Phase 1(05~09강)에서 배운 SELECT, INSERT, UPDATE, DELETE, 정렬/페이징을 하나의 완성된 Repository로 통합합니다.
  * `author` 테이블을 "회원(Member)" 도메인으로 삼아 실제 관리자 백엔드 API 수준의 기능을 구현합니다.
  * 이 프로젝트는 Phase 1의 총정리이자 다음 Phase 2(중·고급)로 넘어가기 위한 기초 체력 점검입니다.

## 2. 강의 계획 작성

### 2.1. 학습 목표
1. **통합 적용:** 지금까지 따로 배운 SELECT/INSERT/UPDATE/DELETE/정렬/페이징을 `MemberRepository` 하나에 녹여서 실전 관리자 기능을 완성할 수 있다.
2. **패턴 정착:** 각 CRUD 기능에 맞는 최적의 jOOQ 메서드 체이닝 패턴을 자연스럽게 선택하고 조합할 수 있다.
3. **테스트 설계:** CRUD 전체를 BDD 패턴으로 검증하는 완전한 통합 테스트를 작성하고 실행할 수 있다.

### 2.2. 학습 내용
1. **목록 조회 (findAll):** `orderBy(lastName ASC) + limit/offset` 페이징 — 09강 응용
2. **단건 조회 (findById):** `selectFrom(AUTHOR).where(AUTHOR.ID.eq(id)).fetchOneInto()` — 05강 응용
3. **등록 (create):** `UpdatableRecord.store()` — 06강 응용
4. **수정 (update):** `dsl.update(AUTHOR).set(...).where(...)` — 07강 응용
5. **삭제 (delete):** `dsl.deleteFrom(AUTHOR).where(...)` — 07강 응용
6. **시각화 (Mermaid):** MemberRepository 전체 아키텍처 + 각 메서드 → SQL 매핑 다이어그램

### 2.3. 기술 선택 및 개발환경
* **도메인:** `author` 테이블 = 회원(Member) 역할
* **언어:** Java 17+, Kotlin 1.9+  
* **프레임워크:** Spring Boot 3.x (spring-boot-starter-jooq)
* **테스트:** `@SpringBootTest` + `@Transactional`
* **조회 반환:** `Optional<Author>` (Java), `Author?` (Kotlin) + `List<Author>` 페이징

### 2.4. 개발 단계
1. `findAll(page, size)` → `List<Author>` (lastName ASC 정렬)
2. `findById(id)` → `Optional<Author>` (없으면 empty)
3. `create(firstName, lastName)` → `AuthorRecord` (생성된 레코드 반환)
4. `update(id, firstName, lastName)` → `int` (영향 행 수)
5. `delete(id)` → `int` (영향 행 수)
6. 각 단계를 BDD given/when/then 테스트로 검증
