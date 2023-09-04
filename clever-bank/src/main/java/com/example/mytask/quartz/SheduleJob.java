package com.example.mytask.quartz;

import com.example.mytask.connection.ConnectionPool;
import com.example.mytask.connection.HikariCPConnectionPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;


@RequiredArgsConstructor
@Slf4j
public class SheduleJob implements Job {
    private static final String SQL_JOB = "UPDATE cleverbank.bank_account " + "SET balance = balance + (balance * ? / 100), " + "    accrual_date = NOW() " + "WHERE DATE_PART('day', NOW() - accrual_date) >= 30";
    private static final String percents = "job.persents";
    private static final String PROPERTIES_DATABASE_FILE_NAME = "application.yml";
    private static final String FILE_NOT_FOUND_EXCEPTION_MESSAGE = "FileNotFoundException";
    private static final String IO_EXCEPTION_MESSAGE = "IOException";
    private static final Properties PROPERTIES = new Properties();
    private final ConnectionPool connectionPool;
    private Long percent;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try (InputStream inputStream = HikariCPConnectionPool.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_DATABASE_FILE_NAME)) {
            PROPERTIES.load(inputStream);
            percent = Long.valueOf(PROPERTIES.getProperty(percents));
        } catch (FileNotFoundException e) {
            log.error(FILE_NOT_FOUND_EXCEPTION_MESSAGE, e);
            throw new RuntimeException(FILE_NOT_FOUND_EXCEPTION_MESSAGE, e);
        } catch (IOException e) {
            log.error(IO_EXCEPTION_MESSAGE, e);
            throw new RuntimeException(IO_EXCEPTION_MESSAGE, e);
        }
        try (Connection connection = connectionPool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SQL_JOB)) {
            preparedStatement.setLong(1, percent);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Error with job", e);
        }
    }
}
