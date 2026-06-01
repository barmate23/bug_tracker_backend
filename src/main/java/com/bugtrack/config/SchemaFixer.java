package com.bugtrack.config;

import org.springframework.boot.CommandLineRunner;
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
    try {
      jdbcTemplate.execute("ALTER TABLE bug_attachments MODIFY COLUMN data LONGBLOB NOT NULL");
    } catch (Exception ignored) {
      // Table may not exist yet on first H2 boot; Hibernate will create it with the right type.
    }
  }
}
