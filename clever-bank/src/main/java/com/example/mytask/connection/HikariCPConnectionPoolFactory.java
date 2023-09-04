package com.example.mytask.connection;

public class HikariCPConnectionPoolFactory implements ConnectionPoolFactory {
    @Override
    public ConnectionPool createPool() {
        return new HikariCPConnectionPool();
    }
}
