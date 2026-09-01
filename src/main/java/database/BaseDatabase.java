package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseDatabase {
    protected static HikariDataSource dataSource;

    //Subclasses should implement this to provide those specific DB configurations
    protected abstract HikariConfig getHikariConfig();

    protected void initialize()
    {
        if(dataSource == null)
        {
            HikariConfig config = getHikariConfig();
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);
        }
    }

    public static Connection getConnection() throws SQLException {
        if(dataSource == null){
            throw new IllegalStateException("DataSource has not been initialized. Call initialize() on a specific DB class first.");
        }
        return dataSource.getConnection();
    }


}
