package com.example.demo;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDbTest {

    @Test
    public void createDatabaseAndTables() {
        String urlDB = "jdbc:postgresql://localhost:5432/postgres";
        try (Connection conn = DriverManager.getConnection(urlDB, "postgres", "postgres");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE jooq_order");
            System.out.println("jooq_order Database created successfully!");
        } catch (Exception e) {
            System.out.println("DB likely exists: " + e.getMessage());
        }

        String urlTarget = "jdbc:postgresql://localhost:5432/jooq_order";
        try (Connection connTarget = DriverManager.getConnection(urlTarget, "postgres", "postgres");
             Statement stmtTarget = connTarget.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/db/migration/V1__init_order_inventory.sql")));
            stmtTarget.executeUpdate(sql);
            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Table creation failed", e);
        }
    }
}
