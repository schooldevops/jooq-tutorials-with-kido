package com.example.demo

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.DriverManager

class CreateDbTest {

    @Test
    fun createDatabaseAndTables() {
        val urlDB = "jdbc:postgresql://localhost:5432/postgres"
        try {
            DriverManager.getConnection(urlDB, "postgres", "postgres").use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeUpdate("CREATE DATABASE jooq_dynamic_search")
                    println("jooq_dynamic_search Database created successfully!")
                }
            }
        } catch (e: Exception) {
            println("DB likely exists: \${e.message}")
        }

        val urlTarget = "jdbc:postgresql://localhost:5432/jooq_dynamic_search"
        try {
            DriverManager.getConnection(urlTarget, "postgres", "postgres").use { connTarget ->
                connTarget.createStatement().use { stmtTarget ->
                    val sql = String(Files.readAllBytes(Paths.get("src/main/resources/db/migration/V1__init_dynamic_search.sql")))
                    stmtTarget.executeUpdate(sql)
                    println("Tables created successfully!")
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Table creation failed", e)
        }
    }
}
