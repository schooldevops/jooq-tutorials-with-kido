# Chapter 19: 멀티테넌시 개발 계획

이번 장에서는 jOOQ `RenderMapping`과 `ThreadLocal`을 결합한 스키마 분리형 멀티테넌시를 구축합니다. 이를 검증하기 위해 두 가지 다른 스키마(예: 로직 상 `tenant_a`, `tenant_b`)를 치환하는 시나리오를 구성합니다.

## 개발 스텝

### 1단계: 프로젝트 환경 설정
- 기존 `codebase`를 바탕으로 Java 및 Kotlin 하위 디렉토리를 복사.
- `settings.gradle` 및 `application.yml` 세팅을 맞춤.

### 2단계: `TenantContext` 유틸리티 제작
- `TenantContext` 클래스 작성 (Java / Kotlin 공통)
  - `ThreadLocal<String>`을 이용해 `tenantId` 저장소 관리.
  - `setCurrentTenant(tenantId)`, `getCurrentTenant()`, `clear()` 기본 메서드 지원.

### 3단계: jOOQ ConfigCustomizer 주입 
- `JooqConfiguration` 또는 빈 오버라이딩을 통해 `DefaultConfigurationCustomizer`를 상속.
- 커스터마이저 내부에서 매 요청 시마다 동작할 Provider 개념 혹은 ExecuteListener 기반 렌더 맵핑 적용. 
*(최신 jOOQ / Spring Boot 통합 환경에서 `Settings` 빈을 프로바이더 형태로 재설정하거나, `VisitListener`를 통해 SQL 빌드 시점에 개입하는 것을 구현)*

### 4단계: `TenantRepository` 빈 만들기
- 간단히 `author` 테이블을 전원 조회(`select * from public.author`)하는 메서드를 제공합니다.
- 개발자는 해당 쿼리를 짤 때 `public` 스키마(코드 생성된 기본 테이블) 기준으로 작성합니다.

### 5단계: 통합 테스트 작성 및 BDD 검증
- `MultiTenancyTest` 클래스 작성
- **`tenant_a` 스키마 매핑 케이스:** 
  - `TenantContext.setCurrentTenant("tenant_a")` 로 설정.
  - 레포지토리를 날린 뒤 실제로 DB에는 `tenant_a.author` 테이블을 찾는 로그(오류 또는 생성된 임시 테이블 내역)가 찧기는지 확인.
- **`tenant_b` 스키마 매핑 케이스:**
  - 동일 로직에서 스키마명만 변환되는지 확정 검증.
- `finally` 블록 등에서 `clear()`를 수행하여 쓰레드 누수를 방지하는지 점검.

*※ 본 예제 테스트에서는 실제 테넌트 스키마 수십개를 DDL로 만들지 않고, 쿼리 렌더링 문자열에서 올바르게 스키마 명이 치환되어 데이터접근 위반 오류가 "변경된 스키마"로부터 나는지 검증하거나, 임시 스키마를 생성하는 방식으로 증명하겠습니다.*
