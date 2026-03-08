package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDbTest {

    @Test
    public void executeDbScript() throws Exception {
        // 1. Create databases
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE jooq_migration_legacy");
        } catch (Exception e) {
            System.out.println("Database legacy creation failed (maybe exists): " + e.getMessage());
        }

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE jooq_migration_target");
        } catch (Exception e) {
            System.out.println("Database target creation failed (maybe exists): " + e.getMessage());
        }

        // 2. Init legacy schema
        initSchema("jdbc:postgresql://localhost:5432/jooq_migration_legacy", "/db/migration/legacy/V1__init_legacy.sql");

        // 3. Init target schema
        initSchema("jdbc:postgresql://localhost:5432/jooq_migration_target", "/db/migration/target/V1__init_target.sql");
    }

    private void initSchema(String url, String scriptPath) throws Exception {
        try (Connection conn = DriverManager.getConnection(url, "postgres", "postgres");
             Statement stmt = conn.createStatement()) {

            var is = getClass().getResourceAsStream(scriptPath);
            if (is == null) throw new IllegalArgumentException("Cannot find script: " + scriptPath);
            String sql = FileCopyUtils.copyToString(new InputStreamReader(is, StandardCharsets.UTF_8));
            stmt.execute(sql);
            System.out.println("Successfully executed script against " + url);
        }
    }
}
