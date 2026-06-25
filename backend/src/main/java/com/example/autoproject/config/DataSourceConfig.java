package com.example.autoproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig implements ApplicationRunner {

    private final DataSource dataSource;

    public DataSourceConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing database schema...");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/schema.sql"));
        populator.execute(dataSource);
        log.info("Database schema initialized successfully");
    }
}
