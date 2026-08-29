package database;

import com.zaxxer.hikari.HikariConfig;

public class PostgresDatabase extends BaseDatabase {

    static {
        new PostgresDatabase().initialize();
    }
    @Override
    protected HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("123");
        return config;
    }
}
