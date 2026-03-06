package com.example.mapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;

@SpringBootTest
class MyBatisErrorTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("MyBatis는 쿼리에 오타가 있어도 컴파일을 통과하며, 런타임에 에러가 발생한다")
    void mybatisRuntimeErrorTest() {
        // e_mail 이라는 컬럼은 존재하지 않으므로, 호출하는 시점에 런타임 예외가 발생함을 단언
        assertThatThrownBy(() -> userMapper.findByEmail("test@example.com"))
                .isInstanceOf(BadSqlGrammarException.class);
    }
}
