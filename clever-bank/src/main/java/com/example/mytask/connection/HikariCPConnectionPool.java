package com.example.mytask.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class HikariCPConnectionPool implements ConnectionPool {
    private static final HikariConfig CONFIG = new HikariConfig();
    private static final Properties PROPERTIES = new Properties();
    private static final String PROPERTIES_DATABASE_FILE_NAME = "application.yml";
    private static final String PROPERTY_URL = "db.url";
    private static final String PROPERTY_USER = "db.user";
    private static final String PROPERTY_PASSWORD = "db.password";
    private static final String PROPERTY_MAX_POOL_SIZE = "db.maxpoolsize";
    private static final String PROPERTY_DRIVER = "db.driver";
    private static final String DATABASE_URL;
    private static final String DATABASE_USER;
    private static final String DATABASE_PASSWORD;
    private static final String DATABASE_DRIVER;
    private static final String DATABASE_MAX_POOL_SIZE;
    private static final HikariDataSource ds;

    private static final String FILE_NOT_FOUND_EXCEPTION_MESSAGE = "FileNotFoundException";

    private static final String IO_EXCEPTION_MESSAGE = "IOException";

    private static final String DATABASE_CONNECTED_SUCCESSFUL_MESSAGE = "Database connected successful";

    static {
        try (InputStream inputStream = HikariCPConnectionPool.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_DATABASE_FILE_NAME)) {
            PROPERTIES.load(inputStream);
            DATABASE_URL = PROPERTIES.getProperty(PROPERTY_URL);
            DATABASE_USER = PROPERTIES.getProperty(PROPERTY_USER);
            DATABASE_PASSWORD = PROPERTIES.getProperty(PROPERTY_PASSWORD);
            DATABASE_DRIVER = PROPERTIES.getProperty(PROPERTY_DRIVER);
            DATABASE_MAX_POOL_SIZE = PROPERTIES.getProperty(PROPERTY_MAX_POOL_SIZE);
        } catch (FileNotFoundException e) {
            log.error(FILE_NOT_FOUND_EXCEPTION_MESSAGE, e);
            throw new RuntimeException(FILE_NOT_FOUND_EXCEPTION_MESSAGE, e);
        } catch (IOException e) {
            log.error(IO_EXCEPTION_MESSAGE, e);
            throw new RuntimeException(IO_EXCEPTION_MESSAGE, e);
        }
        CONFIG.setJdbcUrl(DATABASE_URL);
        CONFIG.setUsername(DATABASE_USER);
        CONFIG.setPassword(DATABASE_PASSWORD);
        CONFIG.setDriverClassName(DATABASE_DRIVER);
        CONFIG.setMaximumPoolSize(Integer.parseInt(DATABASE_MAX_POOL_SIZE));
        ds = new HikariDataSource(CONFIG);
        log.info(DATABASE_CONNECTED_SUCCESSFUL_MESSAGE);
    }

    @Override
    public ProxyConnection getConnection() throws SQLException {
        return (ProxyConnection) ds.getConnection();
    }
}
