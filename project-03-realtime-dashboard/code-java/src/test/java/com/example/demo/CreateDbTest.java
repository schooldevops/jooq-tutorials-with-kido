package com.example.demo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

public class CreateDbTest {

    @Test
    public void createDatabaseAndTables() {
        String urlDB = "jdbc:postgresql://localhost:5432/postgres";
        try (Connection conn = DriverManager.getConnection(urlDB, "postgres", "postgres");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE jooq_dashboard");
            System.out.println("jooq_dashboard Database created successfully!");
        } catch (Exception e) {
            System.out.println("DB likely exists: " + e.getMessage());
        }

        String urlTarget = "jdbc:postgresql://localhost:5432/jooq_dashboard";
        try (Connection connTarget = DriverManager.getConnection(urlTarget, "postgres", "postgres");
             Statement stmtTarget = connTarget.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/db/migration/V1__init_dashboard.sql")));
            stmtTarget.executeUpdate(sql);
            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Table creation failed", e);
        }
    }
}
