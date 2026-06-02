package com.bugtrack.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaFixer implements CommandLineRunner {
  private final JdbcTemplate jdbcTemplate;

  public SchemaFixer(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(String... args) {
    String databaseProduct = jdbcTemplate.execute(
        (ConnectionCallback<String>) connection -> connection.getMetaData().getDatabaseProductName());
    if (!"MySQL".equalsIgnoreCase(databaseProduct)) {
      return;
    }

    jdbcTemplate.execute("""
        ALTER TABLE bugs
        MODIFY status ENUM('OPEN','IN_PROGRESS','HOLD','RESOLVED','CLOSED') NOT NULL
        """);
  }
}
