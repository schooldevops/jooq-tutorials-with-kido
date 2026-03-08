# Project 04: 동적 필터링 검색 엔진 구축 진행 단계

## Step 1: PostgreSQL 스키마 및 DB 설정 초기화
- `jooq_dynamic_search` 이름으로 Database 생성
- Flyway V1 마이그레이션 스크립트를 통한 `product` 테이블 및 인덱스 세팅
- Gradle task를 통해 Java/Kotlin jOOQ 빌드 수행

## Step 2: Request/Response DTO 정의
- `ProductSearchRequestDto`: 카테고리, 상태코드, 가격검색범위(min/max), 키워드 등 옵셔널 필드 구성
- `ProductDto`: 조회 결과 응답용 Data Object

## Step 3: 리포지토리(Repository) 구현
- `ProductSearchRepository` 에서 jOOQ `Condition` 동적 조합 로직 체이닝.
- 파라미터 유무에 따른 조건 삽입(`var conditions = new ArrayList<Condition>()`)
- 최종 `selectFrom(PRODUCT).where(conditions)...` 처리

## Step 4: 서비스(Service) 구현 및 BDD 테스트
- `ProductSearchService`에서 비즈니스 로직 제공 (또는 단순 위임)
- 통합 테스트(`ProductSearchServiceTest`)를 작성하여 **조건 1개**, **조건 3개**, **조건 0개**, **존재하지 않는 조건** 등을 각각 주입해보고, 실제 반환되는 리스트의 크기 및 내용을 Assertion
