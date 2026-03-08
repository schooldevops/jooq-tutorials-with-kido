# Chapter 18: CQRS 하이브리드 - 실습 개발 플랜

## 1. 프로젝트 설정
- `build.gradle`에 `spring-boot-starter-data-jpa` 라이브러리 추가
- `application.yml`에 JPA DDL Auto 등의 설정은 (jOOQ DB를 이미 사용 중이므로) `none` 이나 `validate` 수준으로 억제 (수동 제어 위주) 혹은 생략. (여기선 ddl-auto 제어는 큰 의미 없지만 주의사항으로 기록)

## 2. JPA 컴포넌트 구현
- `AuthorEntity`: DB의 `author` 테이블 맵핑 (id, first_name, last_name)
- `AuthorJpaRepository`: `JpaRepository<AuthorEntity, Integer>` 상속

## 3. jOOQ 컴포넌트 구현
- `AuthorJooqRepository`: jOOQ `DSLContext` 주입. 단순 `findById(id)` 셀렉트 메서드 구현

## 4. 통합 Service 및 Test 구현 (`AuthorService`)
- JPA로 `save()` 후 jOOQ로 바로 조회(Read)하는 로직 작성
- **BDD Test 1:** `save()` (without flush) -> jOOQ 조회 시도 -> **실패(null 반환)함을 검증**
- **BDD Test 2:** `saveAndFlush()` -> jOOQ 조회 시도 -> **성공(값 존재)함을 검증**
