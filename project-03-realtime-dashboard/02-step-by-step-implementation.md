# Project 03: Step-by-Step Implementation Guide

실시간 대시보드 API (Real-time Dashboard API)의 구현 및 테스트 진행을 위한 가이드입니다.

## Step 1: 프로젝트 셋업 및 DB 설정
- 베이스 코드 저장소(`codebase`)로 부터 `project-03-realtime-dashboard/code-java`, `code-kotlin` 프로젝트를 스캐폴딩합니다.
- 기본 구성(build.gradle 호환성 적용)을 수행하고 `jooq_dashboard` 라는 이름의 새 DB를 바라보게 설정합니다. (\application.yml)

## Step 2: Flyway Migration 및 jOOQ 클래스 자동 생성
- `V1__init_dashboard.sql` 을 작성합니다. `daily_sales` 테이블 생성 스크립트를 포함합니다.
- 테스트 인스턴스(`CreateDbTest.java`)를 통해 DB 및 테이블을 초기화합니다.
- `./gradlew generateJooq` 를 실행해 jOOQ 레코드 및 테이블 클래스들을 소환합니다.

## Step 3: DTO 계층 설계
분석 결과를 한 번에 받아줄 전용 DTO를 선언합니다.
```java
// Java의 경우 Record로 구현
public record ProductSalesRankDto(
    LocalDate saleDate,
    Long productId,
    String category,
    BigDecimal revenue,        // 금일 매출
    BigDecimal previousRevenue, // 전일 매출 (Lag 활용)
    Integer rank               // 일별/카테고리별 순위 (Rank 활용)
) {}
```
증감률 로직은 DTO 내부 또는 Service에서 추가 처리합니다 (`(금일 - 전일) / 전일 * 100`).

## Step 4: Repository (데이터 접근 계층) 구현
### `DashboardRepository.getDailyDashboard(LocalDate targetDate)`
- **조회 대상 일자 및 어제 대상 데이터 필터링**: `targetDate`와 `targetDate.minusDays(1)`의 데이터를 `where` 범위 스캔.
- **Select Projection 구성**:
    - `DAILY_SALES.SALE_DATE`
    - `DAILY_SALES.PRODUCT_ID`
    - `DAILY_SALES.CATEGORY`
    - `DAILY_SALES.REVENUE`
    - `LAG` 윈도우 함수
    - `RANK` 윈도우 함수
- 조회된결과를 `fetchInto()`로 매핑하여 리스트로 바로 반환하게 작성합니다.

## Step 5: Service 계층 및 통합 테스트
- `DashboardService`를 통해 Repository를 호출하고 클라이언트 응답용으로 다듬는 로직을 수행합니다.
- `DashboardServiceTest` 에 임의의 Mocking Data (ex. 여러 날짜, 여러 제품군의 매출) 삽입 로직을 `setUp`에 둡니다.
- 조회 테스트를 통해 전일대비 역산 데이터(`previousRevenue`)가 맞게 끌려왔는지, 그리고 동위 매출일 때의 `rank` 처리 무결성을 Assert 로 검증합니다.
