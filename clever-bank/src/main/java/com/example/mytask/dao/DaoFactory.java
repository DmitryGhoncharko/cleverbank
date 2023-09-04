package com.example.mytask.dao;

import java.sql.Connection;

public interface DaoFactory {
    BankAccountDao createBankAccountDao(Connection connection);

    UserDao createUserDao(Connection connection);
}
