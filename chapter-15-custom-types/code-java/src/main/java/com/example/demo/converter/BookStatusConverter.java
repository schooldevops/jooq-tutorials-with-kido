package com.example.demo.converter;

import com.example.demo.type.BookStatus;
import org.jooq.Converter;

/**
 * DB VARCHAR ↔ BookStatus Enum 쌍방향 변환 컨버터
 *
 * from(): DB값 → Java Enum (읽기)
 * to():   Java Enum → DB값 (쓰기)
 */
public class BookStatusConverter implements Converter<String, BookStatus> {

    @Override
    public BookStatus from(String dbValue) {
        return dbValue == null ? null : BookStatus.valueOf(dbValue);
    }

    @Override
    public String to(BookStatus userValue) {
        return userValue == null ? null : userValue.name();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<BookStatus> toType() {
        return BookStatus.class;
    }
}
