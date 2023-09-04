package com.example.mytask.service;

import com.example.mytask.connection.ConnectionPool;
import com.example.mytask.connection.HikariCPConnectionPool;

import java.sql.Connection;

public class SimpleServiceFactory implements ServiceFactory{
    private final ConnectionPool connectionPool = new HikariCPConnectionPool();
    @Override
    public BankAccountService createBankAccount() {
        return new SimpleBankAccountService(connectionPool);
    }
}
