# Chapter 19: 멀티테넌시(Multi-tenancy)

## 1. 수업 개요

본 챕터에서는 클라우드 기반 애플리케이션 등에서 하나의 프로비저닝된 인프라 위에서 논리적으로 다수의 고객(테넌트)을 격리하는 **멀티테넌시 시스템**을 jOOQ로 어떻게 구현하는지 학습합니다. 

현대 B2B SaaS 환경에서는 스키마나 데이터베이스 분리, 파티셔닝 등 다양한 멀티테넌스 전략을 사용하며, 이를 가장 유연하고 안전하게 쿼리에 반영하는 법을 배웁니다.

## 2. 학습 목표

1. 멀티테넌시의 다양한 접근 전략 이해 (Shared Database / Shared Schema / Separate Schema / Separate DB)
2. jOOQ의 `RenderMapping` 기능 완벽한 이해
3. 런타임에 쓰레드 별(ThreadLocal) 문맥 기반으로 SQL에서 기본 스키마(public)를 대상 테넌트 스키마로 동적 치환하는 기법 습득
4. Java / Kotlin 에서 ThreadLocal 을 활용해 `TenantContext`를 구축하는 설계 이해

## 3. 선수 지식

- ThreadLocal 및 애플리케이션 컨텍스트의 생명주기
- jOOQ `Configuration`과 `ExecuteListener` 에 대한 기본 이해
- 기초 SQL 상식 (Schema, Table 구조)

## 4. 실습 환경

- **언어:** Java 17+, Kotlin 1.9+
- **프레임워크:** Spring Boot 3.x
- **데이터베이스:** PostgreSQL 15+ (테스트 시 가상 스키마를 통해 검증)
- **주요 라이브러리:** jOOQ 3.19.x

## 5. 수업 시나리오

1. **설계 토크:** B2B SaaS 환경에서 멀티테넌시가 필요한 이유와 흔한 안티패턴.
2. **Context Setup:** ThreadLocal 기반의 `TenantContext` 유틸리티 제작.
3. **jOOQ Customizer 구현:** Spring Boot AutoConfiguration이 제공하는 `DefaultConfigurationCustomizer`를 상속(구현)하여, `RenderMapping`을 동적으로 추가.
4. **테스트를 통한 동적 렌더링 검증:** 테스트 코드 내에서 `TenantContext.setTenant("tenant_a")` 등 값을 부여한 뒤 쿼리를 날렸을 때 실제 렌더링된 SQL과 결과 검증.

## 6. 기대 효과

- 운영 중 멀티테넌트를 위한 쿼리를 비즈니스 로직과 완벽히 격리할 수 있습니다.
- 개발자는 테넌트에 신경쓰지 않고 하나의 코드로 비즈니스를 작성하고, jOOQ 프레임워크 레벨에서 횡단 관심사로 테넌트 식별 및 스키마 라우팅을 자동화하게 됩니다.
