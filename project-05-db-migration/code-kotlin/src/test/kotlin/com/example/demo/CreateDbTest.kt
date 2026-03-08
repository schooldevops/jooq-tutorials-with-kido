package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.sql.DriverManager

class CreateDbTest {

    @Test
    fun executeDbScript() {
        // 1. Create databases
        try {
            DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres").use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeUpdate("CREATE DATABASE jooq_migration_legacy")
                }
            }
        } catch (e: Exception) {
            println("Database legacy creation failed (maybe exists): " + e.message)
        }

        try {
            DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres").use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeUpdate("CREATE DATABASE jooq_migration_target")
                }
            }
        } catch (e: Exception) {
            println("Database target creation failed (maybe exists): " + e.message)
        }

        // 2. Init legacy schema
        initSchema("jdbc:postgresql://localhost:5432/jooq_migration_legacy", "/db/migration/legacy/V1__init_legacy.sql")

        // 3. Init target schema
        initSchema("jdbc:postgresql://localhost:5432/jooq_migration_target", "/db/migration/target/V1__init_target.sql")
    }

    private fun initSchema(url: String, scriptPath: String) {
        DriverManager.getConnection(url, "postgres", "postgres").use { conn ->
            conn.createStatement().use { stmt ->
                val inputStream = javaClass.getResourceAsStream(scriptPath)
                    ?: throw IllegalArgumentException("Cannot find script: $scriptPath")
                val sql = FileCopyUtils.copyToString(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                stmt.execute(sql)
                println("Successfully executed script against $url")
            }
        }
    }
}
