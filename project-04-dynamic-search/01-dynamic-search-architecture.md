# Project 04: 동적 필터링 검색 엔진 아키텍처

## 1. 개요 및 목표
다양한 옵션을 통한 상품 검색 기능은 이커머스나 백오피스 서비스에서 가장 핵심적인 부분입니다. 사용자가 입력하는 조건(카테고리, 가격대, 제조사, 상태, 재고 등)은 동적으로 변하므로, 쿼리 역시 동적으로 생성되어야 합니다.
본 프로젝트에서는 jOOQ의 DSL 기반 **Builder 패턴**과 `Condition` 객체의 체이닝 기법을 활용하여 10개 이상의 다중 검색 파라미터를 유연하게 처리하는 **동적 쿼리 엔진**을 구축합니다.

## 2. 데이터베이스 스키마 (`product`)
하나의 통합 테이블 `product`를 사용하여 여러 상태 및 조건 필터링을 구성합니다.
* `id`: PK (BIGINT)
* `name`: 상품명 (VARCHAR)
* `category`: 카테고리 (VARCHAR - ex. ELECTRONICS, BOOKS, CLOTHING)
* `price`: 가격 (DECIMAL)
* `stock_status`: 재고 상태 (VARCHAR - IN_STOCK, OUT_OF_STOCK)
* `status`: 판매 상태 (VARCHAR - ACTIVE, INACTIVE, DELETED)
* `manufacturer`: 제조사 (VARCHAR)
* `created_at`: 등록일 (TIMESTAMP)

## 3. 동적 쿼리 구성 전략
Spring Boot 환경에서 넘어오는 HTTP Parameter들을 DTO (`ProductSearchRequestDto`) 로 바인딩 받아, jOOQ `Condition` 리스트나 `BooleanBuilder` 형식으로 누적합니다.
- 조건 값이 null 이거나 비어있으면 `DSL.noCondition()` 을 반환하거나 아예 체인에 추가하지 않습니다.
- 모든 검색 조건을 모은 `Collection<Condition>` 을 `dsl.selectFrom().where(...)` 절에 넘겨 최종 쿼리를 렌더링합니다.

## 4. 컴포넌트 설계
- **Controller**: 페이징 및 다중 검색 파라미터를 수용하는 `GET /api/products` 엔드포인트 제공. (선택 구현)
- **ProductSearchService**: 컨트롤러(또는 테스트)로부터 DTO를 받아 Repository 호출.
- **ProductSearchRepository**: jOOQ DSL 기반 동적 쿼리 렌더링 및 `fetchInto` 매핑 수행.
