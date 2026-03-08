# 20. 실행 라이프사이클과 인터셉터 (ExecuteListener)

## 1. 개요 및 학습 목표
- **개요:** jOOQ는 쿼리가 생성되고 실행되며 결과를 매핑하는 전체 과정을 제어할 수 있는 강력한 라이프사이클 훅(Lifecycle Hook)인 `ExecuteListener`를 제공합니다.
- **학습 목표:**
   - jOOQ의 `ExecuteListener` 인터페이스를 이해합니다.
   - SQL 실행 로깅, 실행 시간 측정(Slow Query 감지), 익셉션 변환, 보안 감사(Audit) 등을 인터셉터를 통해 투명하게 처리하는 방법을 학습합니다.

## 2. 주요 개념
- **ExecuteListener Lifecycle:** jOOQ의 쿼리 실행은 `start`, `renderStart`, `renderEnd`, `prepareStart`, `prepareEnd`, `bindStart`, `bindEnd`, `executeStart`, `executeEnd`, `fetchStart`, `fetchEnd`, `end` 등 세밀하게 분할된 이벤트를 발생시킵니다.
- **DefaultExecuteListener:** 모든 메서드를 기본으로 구현한 어댑터 클래스로, 필요한 라이프사이클 훅만 오버라이드하여 사용할 수 있습니다.
- **활용 사례:** 
   - 쿼리 성능 프로파일링 (slow 쿼리 로깅)
   - 보안 감사 로깅 (누가 어떤 쿼리를 실행했나)
   - Spring Exception 변환 (jOOQ 기본 리스너 역할)

## 3. 실습 시나리오
- **Java 및 Kotlin 프로젝트:**
   1. 데이터 등록(INSERT), 조회(SELECT), 예외 발생(Bad Grammar) 등의 액션을 수행하는 Repository 작성.
   2. `PerformanceListener`를 작성하여 각 쿼리의 총 실행 시간(`executeStart` ~ `executeEnd`)을 측정하고, 콘솔에 SQL문과 함께 로깅합니다.
   3. `ExecuteListenerProvider`를 통해 설정(Configuration)에 리스너를 결합.
   4. 통합 테스트에서 로깅이 정상적으로 처리되는지 확인합니다.
