# Project 03: Real-time Dashboard API Architecture

## 1. Overview
이 프로젝트는 **"실시간 대시보드 API (Real-time Dashboard API)"** 구현을 목표로 합니다.
실무의 대시보드 요구사항에서는 단순한 누적/그룹핑 합계뿐만 아니라 전일 대비 변동률(Change Rate) 및 카테고리 내 매출 순위(Ranking)와 같은 분석형 질의가 빈번하게 발생합니다. 이를 애플리케이션 메모리가 아닌, **데이터베이스의 윈도우 함수(Window Functions)** 와 **jOOQ**를 결합하여 압도적인 성능으로 해결하는 아키텍처 패턴을 실습합니다.

## 2. DB Schema Design
일별 매출 집계(Daily Sales)에 초점을 맞추어 1개의 단일 팩트 테이블을 정의합니다.

### 2.1. `daily_sales` 테이블
특정 일자, 상품, 카테고리 단위의 누적 매출을 저장합니다.
- `id` (PK, Serial)
- `sale_date` (Date, 집계 일자)
- `product_id` (Long, 상품 ID)
- `category` (Varchar, 카테고리명)
- `revenue` (Decimal, 당일 총 매출액)
- `updated_at` (Timestamp)

*복합 인덱스*: `(sale_date, category)` 생성 예정 (조회 최적화 목적).

## 3. Key Concepts (jOOQ Window Functions)

### 3.1. 전일 대비 증감 (Lag Function)
- `LAG(revenue) OVER (PARTITION BY product_id ORDER BY sale_date)`
- 어제 기준의 매출액을 가져와 오늘의 매출액과 비교함으로써 증감률을 즉시 산출합니다.
- jOOQ syntax: `lag(DAILY_SALES.REVENUE).over().partitionBy(DAILY_SALES.PRODUCT_ID).orderBy(DAILY_SALES.SALE_DATE)`

### 3.2. 일일 카테고리 내 랭킹 (Rank Function)
- `RANK() OVER (PARTITION BY sale_date, category ORDER BY revenue DESC)`
- 특정 일자, 동일 카테고리 내에서 매출 순위를 계산합니다.
- jOOQ syntax: `rank().over().partitionBy(DAILY_SALES.SALE_DATE, DAILY_SALES.CATEGORY).orderBy(DAILY_SALES.REVENUE.desc())`

## 4. Architecture Pattern
- **Layered Architecture**: Controller -> Service -> Repository
- **Data Transfer**:
    - `DashboardResponseDto` 응답 객체 하나에 현재 매출, 전일 매출, 증감률, 당일 랭킹(순위) 정보를 깔끔하게 말아서 API로 제공하는 구조입니다.
    - jOOQ의 `fetchInto(DashboardResponseDto.class)`를 적극 활용하여 DTO로 직행하는 프로젝션을 구성합니다.
