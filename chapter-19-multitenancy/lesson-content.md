# Chapter 19: 멀티테넌시 (RenderMapping 활용)

## 1. 멀티테넌시(Multi-tenancy) 접근 방식

SaaS 모델에서 멀티테넌시를 구현하는 전략은 크게 3가지로 나뉩니다.

1. **Database-per-Tenant:** 테넌트마다 물리적인 DB 인스턴스를 격리. (보안과 확장이 좋지만 비용 큼)
2. **Schema-per-Tenant:** 하나의 DB 안에서 테넌트별로 `Schema`를 분리. (적절한 격리 수준, 스키마 유지보수 및 쿼리 핸들링 이슈)
3. **Shared-Schema (Discriminator column):** 같은 스키마/테이블을 쓰되 row마다 `tenant_id` 값을 갖게 함. (저렴하고 관리가 단순하지만 쿼리 누수 취약점 존재)

이 챕터에서는 B2B 애플리케이션에서 가장 가성비 높게 적용하는 **Schema-per-Tenant** 방식을 jOOQ로 해결합니다.

## 2. jOOQ의 `RenderMapping`

jOOQ의 가장 강력한 기능 중 하나는 "SQL을 파싱/렌더링 하는 시점"에 AST(Abstract Syntax Tree)를 변형할 수 있다는 것입니다.

`RenderMapping`은 쿼리에 있는 기본 스키마(예: `public` 스키마)나 테이블명을, 실제 쿼리 실행 직전 특정 이름(예: `tenant_a`)으로 덮어치기(Mapping) 하는 기능입니다. 
따라서 `public.author` 와 같이 짜여진 소스코드 상의 DSL 쿼리가, `RenderMapping`을 만나면 출력 단계에서 `tenant_a.author` 로 바뀌어 실행됩니다.

## 3. 동적 RenderMapping을 위한 ThreadLocal 결합

Spring 웹 애플리케이션은 기본적으로 쓰레드 단위(1 Request = 1 Thread)로 동작합니다.
따라서 다음과 같은 로직이 요구됩니다.

1. **HTTP 필터 / 컨트롤러 진입점:** 요청에 포함된 헤더(예: `X-Tenant-ID`)나 JWT 토큰에서 테넌트 값을 추출.
2. **`TenantContext` 저장:** 추출한 값을 `ThreadLocal` 등을 사용하여 문맥 보관.
3. **jOOQ `VisitListener` 또는 빈 오버라이딩:** 실행 시점에 `TenantContext`에 값이 있으면 스키마 매핑 룰을 적용.

```java
// 핵심 원리
Settings settings = new Settings()
    .withRenderMapping(new RenderMapping()
    .withSchemata(
        new MappedSchema().withInput("public")
            .withOutput(TenantContext.getCurrentTenant()) // 동적 적용!
    ));
```

*(참고: 실제 구현에는 ThreadLocal 값을 지우는 cleanup(finally) 처리가 필수적입니다.)*

## 4. 실무 주의점
- `RenderMapping`을 적용하면 캐시된 쿼리 문자열과 충돌이 있을 수 있으므로 jOOQ의 일부 내부 구성을 파악해야 합니다.
- 스키마가 1,000개 이상으로 넘어가면 다중 DB 아키텍처 혼용 또는 별도의 DB 라우팅(예: Spring 의 `AbstractRoutingDataSource`)을 병행할 수 있습니다. 
