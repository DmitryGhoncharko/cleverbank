package com.example.mytask.util;

import com.example.mytask.connection.ConnectionPool;
import com.example.mytask.exception.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;
public final class ConnectionManager {
    private ConnectionManager(){

    }
    public static void rollbackTransaction(Connection connection) throws ServiceException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }
    public static void commitTransaction(Connection connection) throws ServiceException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }
    public static void startTransaction(Connection connection) throws ServiceException {
        try{
            connection.setAutoCommit(false);
        }catch (SQLException e){
            throw new ServiceException();
        }
    }
    public static void closeConnection(Connection connection) throws ServiceException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }
    public static Connection getConnection(ConnectionPool connectionPool) throws ServiceException {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }
}
