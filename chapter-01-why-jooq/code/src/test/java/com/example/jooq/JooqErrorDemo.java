package com.example.jooq;

public class JooqErrorDemo {

    /**
     * jOOQ를 활용한 Type-safety 시연용 코드 블럭입니다.
     * 이 코드는 주석을 해제할 경우 즉시 컴파일 에러(Unresolved reference)를 발생시킵니다.
     * 런타임(서버 실행 중)까지 가지 않고 개발자의 오타를 사전에 완벽히 차단합니다.
     */
    public void demonstrateCompileError() {
        String emailToSearch = "test@example.com";

        /*
        // ----------------------------------------------------
        // [시연] jOOQ DSL 작성 - 필드명 오타 시도
        // ----------------------------------------------------
        
        dslContext.selectFrom(USERS.USERS) // 테이블 참조
            // 오타: E_MAIL 로 접근 시도
            .where(USERS.USERS.E_MAIL.eq(emailToSearch)) // 🚨 IDE 단계에서 미리 에러 감지 (Cannot resolve symbol 'E_MAIL')
            .fetchOneInto(User.class);
            
        */
        
        System.out.println("jOOQ 코드는 오타가 있을 경우 빌드조차 되지 않으므로 운영 장애를 원천 봉쇄합니다.");
    }
}
