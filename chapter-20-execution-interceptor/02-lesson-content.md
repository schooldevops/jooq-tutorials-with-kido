# Chapter 20: 실행 라이프사이클과 인터셉터

## 1. ExecuteListener 소개

jOOQ의 `ExecuteListener`는 쿼리 실행의 매 단계마다 개입할 수 있는 강력한 인터셉터 메커니즘입니다. Spring AOP나 JDBC 로거(Log4jdbc 등)로도 쿼리 로깅은 가능하지만, jOOQ의 `ExecuteListener`는 jOOQ가 렌더링하는 네이티브 AST 객체(Query, Select 등)에 직접 접근할 수 있다는 엄청난 장점이 있습니다.

스프링 부트 환경에서 jOOQ 빈 자동 구성 시, 기본적으로 jOOQ DataAccess 예외를 스프링의 DataAccessException 계층으로 변환하는 `JooqExceptionTranslator` 역시 이 `ExecuteListener` 기반으로 수립되어 있습니다.

## 2. 라이프사이클 이벤트 (주요 훅)

- `renderStart` / `renderEnd`: SQL 문자열 생성 전후
- `prepareStart` / `prepareEnd`: `PreparedStatement` 생성 전후
- `bindStart` / `bindEnd`: 바인딩 변수에 값 주입 전후
- `executeStart` / `executeEnd`: 실제 DB 쿼리 실행 전후
- `fetchStart` / `fetchEnd`: (Select의 경우) `ResultSet`에서 데이터를 읽어오기 전후

## 3. 구현 패턴: Slow Query Logger

가장 흔하게 사용되는 실무 패턴은 "쿼리 실행 시간 측정" 및 "Slow Query 로깅"입니다.

```java
import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;

public class PerformanceListener extends DefaultExecuteListener {
    @Override
    public void executeStart(ExecuteContext ctx) {
        // 실행 직전의 나노 타임을 ctx의 data 맵에 저장
        ctx.data("time", System.nanoTime());
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        Long startTime = (Long) ctx.data("time");
        if (startTime != null) {
            long executionTime = (System.nanoTime() - startTime) / 1_000_000;
            // 로깅 예시
            System.out.println("Query Execution Time: " + executionTime + "ms");
            System.out.println("Executed SQL: " + ctx.sql());
        }
    }
}
```

## 4. 스프링 부트에서의 등록

스프링 빈으로 컨텍스트에 등록하면, jOOQ Auto-configuration이 이를 자동으로 수집합니다.

```java
@Configuration
public class JooqConfig {
    @Bean
    public DefaultExecuteListenerProvider performanceListenerProvider() {
        return new DefaultExecuteListenerProvider(new PerformanceListener());
    }
}
```

## 5. 실무에서의 응용과 한계

- **감사(Audit):** `System.out`이나 로그 파일뿐 아니라 외부 통계 서버나 APM 도구(New Relic, Datadog 등)의 메트릭 수집기로 쿼리와 소요 시간을 포워딩할 수 있습니다.
- **주의점:** `ExecuteContext.data()` 맵을 이용하면 스레드 안전(Thread-Safe)하게 단일 쿼리의 라이프사이클 안에서 데이터를 공유할 수 있습니다. 스레드 로컬(ThreadLocal)이나 멤버 변수(State)를 쓰면 동시성 위협이 있으므로 절대 피해야 합니다.
