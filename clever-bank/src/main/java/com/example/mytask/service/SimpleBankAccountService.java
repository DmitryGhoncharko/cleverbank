package com.example.mytask.service;

import com.example.mytask.connection.ConnectionPool;
import com.example.mytask.dao.BankAccountDao;
import com.example.mytask.dao.PostgresBankAccountDao;
import com.example.mytask.dao.PostgresTransactionDao;
import com.example.mytask.dao.PostgresUserDao;
import com.example.mytask.dao.TransactionDao;
import com.example.mytask.dao.UserDao;
import com.example.mytask.dto.BankAccountDto;
import com.example.mytask.dto.TransactType;
import com.example.mytask.dto.TransactionDto;
import com.example.mytask.dto.TransactionTypeDto;
import com.example.mytask.dto.UserDto;
import com.example.mytask.exception.DaoException;
import com.example.mytask.exception.ServiceException;
import com.example.mytask.util.CheckSaver;
import com.example.mytask.util.ConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class SimpleBankAccountService implements BankAccountService {
    private final ConnectionPool connectionPool;

    @Override
    public BankAccountDto createBankAccount(BankAccountDto bankAccountDto) throws ServiceException {
        Connection connection = null;
        Timestamp timestamp = new Timestamp(new Date().getTime());
        try {
            connection = ConnectionManager.getConnection(connectionPool);
            ConnectionManager.startTransaction(connection);
            UserDao userDao = new PostgresUserDao(connection);
            UserDto userDtoCreated = userDao.createUser(bankAccountDto.getUserDto());
            BankAccountDto bankAccountDtoAfterUserCreate = BankAccountDto.builder().
                    userDto(userDtoCreated).
                    dateCreated(timestamp).
                    currencyDto(bankAccountDto.getCurrencyDto()).
                    balance(bankAccountDto.getBalance()).
                    bankDto(bankAccountDto.getBankDto()).
                    accrualDate(timestamp).
                    build();
            BankAccountDao bankAccountDao = new PostgresBankAccountDao(connection);
            BankAccountDto bankAccountDtoAfterCreated = bankAccountDao.createBankAccount(bankAccountDtoAfterUserCreate);
            ConnectionManager.commitTransaction(connection);
            return bankAccountDtoAfterCreated;
        } catch (DaoException e) {
            ConnectionManager.rollbackTransaction(connection);
            log.error("Cannot create bank account", e);
            throw new ServiceException("Cannot create bank account", e);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }

    @Override
    public TransactionDto addBalance(BankAccountDto bankAccountDto, double money) throws ServiceException {
        Connection connection = null;
        Timestamp timestamp = new Timestamp(new Date().getTime());
        try {
            connection = ConnectionManager.getConnection(connectionPool);
            ConnectionManager.startTransaction(connection);
            TransactionDao transactionDao = new PostgresTransactionDao(connection);
            TransactionDto transactionDto = TransactionDto.builder().
                    bankAccountDtoTo(bankAccountDto).
                    transactionTypeDto(TransactionTypeDto.builder().
                            id(Long.valueOf(TransactType.ADD_BALANCE.ordinal())).
                            name(TransactType.ADD_BALANCE.name()).
                            build()).
                    transactionDate(timestamp).
                    bankAccountDtoFrom(bankAccountDto).
                    money(money).
                    build();
            TransactionDto transactionDtoAfterCreated = transactionDao.createTransaction(transactionDto);
            BankAccountDao bankAccountDao = new PostgresBankAccountDao(connection);
            Optional<BankAccountDto> bankAccountDtoFromDatabase = bankAccountDao.findById(bankAccountDto.getBankAccountId());
            if (bankAccountDtoFromDatabase.isEmpty()) {
                log.error("Cannot find bank account");
                throw new ServiceException("Cannot find bank account");
            }
            BankAccountDto bankAccountDtoFromOptional = bankAccountDtoFromDatabase.get();
            double balance = bankAccountDtoFromOptional.getBalance();
            double balanceAfterOperation = balance + money;
            BankAccountDto bankAccountDtoAfterOperation = BankAccountDto.builder().
                    bankAccountId(bankAccountDtoFromOptional.getBankAccountId()).
                    userDto(bankAccountDtoFromOptional.getUserDto()).
                    dateCreated(bankAccountDtoFromOptional.getDateCreated()).
                    currencyDto(bankAccountDtoFromOptional.getCurrencyDto()).
                    balance(balanceAfterOperation).
                    bankDto(bankAccountDtoFromOptional.getBankDto()).
                    accrualDate(bankAccountDtoFromOptional.getAccrualDate()).
                    build();
            bankAccountDao.update(bankAccountDtoAfterOperation);
            TransactionDto transactionDtoForReturn = TransactionDto.builder().
                    bankAccountDtoTo(transactionDtoAfterCreated.getBankAccountDtoTo()).
                    transactionTypeDto(TransactionTypeDto.builder().
                            id(Long.valueOf(TransactType.ADD_BALANCE.ordinal())).
                            name(TransactType.ADD_BALANCE.name()).
                            build()).
                    transactionDate(timestamp).
                    bankAccountDtoFrom(transactionDtoAfterCreated.getBankAccountDtoFrom()).
                    money(money).
                    id(transactionDtoAfterCreated.getId()).
                    build();
            ConnectionManager.commitTransaction(connection);
            CheckSaver.SaveCheck(transactionDto);
            return transactionDtoForReturn;
        } catch (DaoException e) {
            ConnectionManager.rollbackTransaction(connection);
            log.error("Cannot add balance", e);
            throw new ServiceException("Cannot add balance", e);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }

    @Override
    public TransactionDto withdrawBalance(BankAccountDto bankAccountDto, double money) throws ServiceException {
        Connection connection = null;
        Timestamp timestamp = new Timestamp(new Date().getTime());
        try {
            connection = ConnectionManager.getConnection(connectionPool);
            ConnectionManager.startTransaction(connection);
            TransactionDao transactionDao = new PostgresTransactionDao(connection);
            TransactionDto transactionDto = TransactionDto.builder().
                    bankAccountDtoTo(bankAccountDto).
                    transactionTypeDto(TransactionTypeDto.builder().
                            id(Long.valueOf(TransactType.WITHDRAW.ordinal())).
                            name(TransactType.WITHDRAW.name()).
                            build()).
                    transactionDate(timestamp).
                    bankAccountDtoFrom(bankAccountDto).
                    money(money).
                    build();
            TransactionDto transactionDtoAfterCreated = transactionDao.createTransaction(transactionDto);
            BankAccountDao bankAccountDao = new PostgresBankAccountDao(connection);
            Optional<BankAccountDto> bankAccountDtoFromDatabase = bankAccountDao.findById(bankAccountDto.getBankAccountId());
            if (bankAccountDtoFromDatabase.isEmpty()) {
                log.error("Cannot find bank account");
                throw new ServiceException("Cannot find bank account");
            }
            BankAccountDto bankAccountDtoFromOptional = bankAccountDtoFromDatabase.get();
            double balance = bankAccountDtoFromOptional.getBalance();
            double balanceAfterOperation = balance - money;
            if (balanceAfterOperation < 0) {
                throw new ServiceException("Cannot withdraw balance");
            }
            BankAccountDto bankAccountDtoAfterOperation = BankAccountDto.builder().
                    bankAccountId(bankAccountDtoFromOptional.getBankAccountId()).
                    userDto(bankAccountDtoFromOptional.getUserDto()).
                    dateCreated(bankAccountDtoFromOptional.getDateCreated()).
                    currencyDto(bankAccountDtoFromOptional.getCurrencyDto()).
                    balance(balanceAfterOperation).
                    bankDto(bankAccountDtoFromOptional.getBankDto()).
                    accrualDate(bankAccountDtoFromOptional.getAccrualDate()).
                    build();
            bankAccountDao.update(bankAccountDtoAfterOperation);
            TransactionDto transactionDtoForReturn = TransactionDto.builder().
                    bankAccountDtoTo(bankAccountDtoAfterOperation).
                    transactionTypeDto(TransactionTypeDto.builder().
                            id(Long.valueOf(TransactType.WITHDRAW.ordinal())).
                            name(TransactType.WITHDRAW.name()).
                            build()).
                    transactionDate(timestamp).
                    bankAccountDtoFrom(bankAccountDtoAfterOperation).
                    money(money).
                    id(transactionDtoAfterCreated.getId()).
                    build();
            ConnectionManager.commitTransaction(connection);
            CheckSaver.SaveCheck(transactionDto);
            return transactionDtoForReturn;
        } catch (DaoException e) {
            ConnectionManager.rollbackTransaction(connection);
            log.error("Cannot withdraw balance", e);
            throw new ServiceException("Cannot withdraw balance", e);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }

    @Override
    public TransactionDto bankAccountTransaction(BankAccountDto bankAccountDtoTo, BankAccountDto bankAccountDtoFrom, double money) throws ServiceException {
        Connection connection = null;
        Timestamp timestamp = new Timestamp(new Date().getTime());
        try {
            connection = ConnectionManager.getConnection(connectionPool);
            ConnectionManager.startTransaction(connection);
            TransactionDao transactionDao = new PostgresTransactionDao(connection);
            TransactionDto transactionDto = TransactionDto.builder().
                    bankAccountDtoTo(bankAccountDtoTo).
                    transactionTypeDto(TransactionTypeDto.builder().
                            id(Long.valueOf(TransactType.TRANSACTION.ordinal())).
                            name(TransactType.TRANSACTION.name()).
                            build()).
                    transactionDate(timestamp).
                    bankAccountDtoFrom(bankAccountDtoFrom).
                    money(money).
                    build();
            TransactionDto transactionDtoAfterCreated = transactionDao.createTransaction(transactionDto);
            BankAccountDao bankAccountDao = new PostgresBankAccountDao(connection);
            Optional<BankAccountDto> bankAccountDtoFromDatabaseTo = bankAccountDao.findById(bankAccountDtoTo.getBankAccountId());
            Optional<BankAccountDto> bankAccountDtoFromDatabaseFrom = bankAccountDao.findById(bankAccountDtoFrom.getBankAccountId());
            if (bankAccountDtoFromDatabaseTo.isEmpty() || bankAccountDtoFromDatabaseFrom.isEmpty()) {
                log.error("Cannot find bank account");
                throw new ServiceException("Cannot find bank account");
            }
            BankAccountDto bankAccountDtoFromOptionalTo = bankAccountDtoFromDatabaseTo.get();
            BankAccountDto bankAccountDtoFromOptionalFrom = bankAccountDtoFromDatabaseFrom.get();
            double balanceAfterOperationTo = bankAccountDtoFromOptionalTo.getBalance() + money;
            double balanceAfterOperationFrom = bankAccountDtoFromOptionalFrom.getBalance() - money;
            BankAccountDto bankAccountDtoAfterOperationTo = BankAccountDto.builder().
                    bankAccountId(bankAccountDtoFromOptionalTo.getBankAccountId()).
                    userDto(bankAccountDtoFromOptionalTo.getUserDto()).
                    dateCreated(bankAccountDtoFromOptionalTo.getDateCreated()).
                    currencyDto(bankAccountDtoFromOptionalTo.getCurrencyDto()).
                    balance(balanceAfterOperationTo).
                    bankDto(bankAccountDtoFromOptionalTo.getBankDto()).
                    accrualDate(bankAccountDtoFromOptionalTo.getAccrualDate()).
                    build();
            bankAccountDao.update(bankAccountDtoAfterOperationTo);
            BankAccountDto bankAccountDtoAfterOperationFrom = BankAccountDto.builder().
                    bankAccountId(bankAccountDtoFromOptionalFrom.getBankAccountId()).
                    userDto(bankAccountDtoFromOptionalFrom.getUserDto()).
                    dateCreated(bankAccountDtoFromOptionalFrom.getDateCreated()).
                    currencyDto(bankAccountDtoFromOptionalFrom.getCurrencyDto()).
                    balance(balanceAfterOperationFrom).
                    bankDto(bankAccountDtoFromOptionalFrom.getBankDto()).
                    accrualDate(bankAccountDtoFromOptionalFrom.getAccrualDate()).
                    build();
            bankAccountDao.update(bankAccountDtoAfterOperationFrom);
            TransactionDto transactionDtoForReturn = TransactionDto.builder().
                    bankAccountDtoTo(bankAccountDtoAfterOperationTo).
                    transactionTypeDto(TransactionTypeDto.builder().
                            id(Long.valueOf(TransactType.TRANSACTION.ordinal())).
                            name(TransactType.TRANSACTION.name()).
                            build()).
                    transactionDate(timestamp).
                    bankAccountDtoFrom(bankAccountDtoAfterOperationFrom).
                    money(money).
                    id(transactionDtoAfterCreated.getId()).
                    build();
            ConnectionManager.commitTransaction(connection);
            CheckSaver.SaveCheck(transactionDto);
            return transactionDtoForReturn;
        } catch (DaoException e) {
            ConnectionManager.rollbackTransaction(connection);
            log.error("Cannot add balance", e);
            throw new ServiceException("Cannot add balance", e);
        } finally {
            ConnectionManager.closeConnection(connection);
        }
    }
}
