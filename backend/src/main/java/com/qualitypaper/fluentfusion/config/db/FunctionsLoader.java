package com.qualitypaper.fluentfusion.config.db;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;

@Configuration
public class FunctionsLoader {

  @Bean
  ApplicationRunner initFunctions(DataSource dataSource) {
    return _ -> {
      try (var conn = dataSource.getConnection()) {
        ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/functions.sql"));
      }
    };
  }
}
