package com.example.mytask.dao;


import java.sql.Connection;

public class PostgresqlDaoFactory implements DaoFactory {

    @Override
    public BankAccountDao createBankAccountDao(Connection connection) {
        return new PostgresBankAccountDao(connection);
    }

    @Override
    public UserDao createUserDao(Connection connection) {
        return new PostgresUserDao(connection);
    }
}
