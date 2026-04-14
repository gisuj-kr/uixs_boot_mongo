package com.uixs.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SqliteConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SqliteConfig.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeDatabase() {
        logger.info("Initializing SQLite databases for JSON document storage...");
        
        String[] tables = {"request_list", "channel", "member", "ia", "comment", "file_info"};
        
        for (String table : tables) {
            String createSql = "CREATE TABLE IF NOT EXISTS " + table + " (id TEXT PRIMARY KEY, doc JSON)";
            jdbcTemplate.execute(createSql);
            logger.info("Checked table: {}", table);
        }
        
        logger.info("SQLite JSON collections successfully verified.");
    }
}
