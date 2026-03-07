# Chapter 10: 기초 프로젝트 - 실습 개발 플랜

## 1. 실습 목적
* Phase 1(05~09강)의 모든 jOOQ 기법을 `MemberRepository` 하나에 통합하여 실전 CRUD 관리자 기능을 완성합니다.
* 5개의 메서드를 각각 BDD 테스트로 검증하며 Phase 1 전체를 최종 확인합니다.

## 2. 사전 준비

### Step 1: 프로젝트 복사 및 설정
* `codebase/java-spring` → `chapter-10-project/code-java`
* `codebase/kotlin-spring` → `chapter-10-project/code-kotlin`
* `build.gradle`: jOOQ 설정 추가 (이전 챕터와 동일)
* `settings.gradle`, `application.yml`: 프로젝트 이름 및 DB URL 변경

## 3. 개발 플랜 (BDD 5단계 CRUD 테스트)

### Step 2: Java - MemberRepository 및 테스트

**`MemberRepository.java` 구성:**

| 메서드 | 반환 타입 | jOOQ 기법 |
|---|---|---|
| `findAll(page, size)` | `List<Author>` | SELECT + orderBy + limit/offset |
| `findById(id)` | `Optional<Author>` | SELECT WHERE + fetchOneInto |
| `create(firstName, lastName)` | `AuthorRecord` | newRecord().store() |
| `update(id, firstName, lastName)` | `int` | DSL UPDATE |
| `delete(id)` | `int` | deleteFrom() |

**단위 테스트 (`MemberRepositoryTest.java`):**
```
테스트 1: findAll(0, 10)
  → 반환 리스트 not null, 비어있지 않음, lastName 오름차순 검증

테스트 2: findById(1)
  → Optional이 present, id=1 데이터 존재

테스트 3: create("Agatha", "Christie")
  → 반환 레코드 not null, ID가 양수

테스트 4: update(1, "William", "Shakespeare-Updated")
  → 반환 값이 1, 재조회 시 lastName이 변경됨

테스트 5: delete
  → 새로 생성한 후 삭제, 삭제 후 재조회 결과가 empty
```

---

### Step 3: Kotlin - MemberRepository 및 테스트
* Java와 동일한 5개 메서드, Kotlin 문법 (`?:` 반환, 단일 표현식 함수)
* 동일한 5단계 BDD 테스트 시나리오

---

## 4. 실행 체크리스트

1. Docker 컨테이너 실행 확인
2. Java: `./gradlew test`
3. Kotlin: `./gradlew test`
4. 모든 5개 테스트 GREEN 확인
