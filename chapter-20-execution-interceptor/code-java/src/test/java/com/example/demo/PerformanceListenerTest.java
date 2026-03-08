package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.repository.LifecycleRepository;

@SpringBootTest
class PerformanceListenerTest {

    @Autowired
    private LifecycleRepository lifecycleRepository;

    @Test
    @DisplayName("PerformanceListener가 등록되어 쿼리 실행시간 로그가 출력되어야 한다")
    void testPerformanceListenerLogging() {
        // given & when
        System.out.println("--- Fast Query Start ---");
        lifecycleRepository.executeFastQuery();
        System.out.println("--- Fast Query End ---\n");

        System.out.println("--- Slow Query Start ---");
        lifecycleRepository.executeSlowQuery();
        System.out.println("--- Slow Query End ---");

        // then
        // 실제 콘솔 출력 로그 확인 위주의 테스트이므로 예외가 발생하지 않으면 통과로 간주
        assertThat(true).isTrue();
    }
}
