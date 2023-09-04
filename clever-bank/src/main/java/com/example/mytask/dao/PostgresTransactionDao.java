package com.example.mytask.dao;

import com.example.mytask.dto.TransactionDto;
import com.example.mytask.exception.DaoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class PostgresTransactionDao implements TransactionDao{
    private static final String SQL_CREATE = "insert into _transaction(bank_account_id_to, transaction_type_id, transaction_date, bank_account_id_from, money) VALUES (?,?,?,?,?)";
    private static final String SQL_FIND_BY_ID = "";
    private final Connection connection;

    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) throws DaoException {
        try{
            Timestamp timestamp = new Timestamp(new Date().getTime());
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1,transactionDto.getBankAccountDtoTo().getBankAccountId());
            preparedStatement.setLong(2,transactionDto.getTransactionTypeDto().getId());
            preparedStatement.setTimestamp(3,timestamp);
            preparedStatement.setLong(4,transactionDto.getBankAccountDtoFrom().getBankAccountId());
            preparedStatement.setDouble(5,transactionDto.getMoney());
            int rowsCreated = preparedStatement.executeUpdate();
            if(rowsCreated>0){
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if(resultSet.next()){
                    TransactionDto transactionDtoAfterSave = TransactionDto.builder().
                            id(resultSet.getLong(1)).
                            bankAccountDtoTo(transactionDto.getBankAccountDtoTo()).
                            transactionTypeDto(transactionDto.getTransactionTypeDto()).
                            transactionDate(timestamp).
                            bankAccountDtoFrom(transactionDto.getBankAccountDtoFrom()).
                            money(transactionDto.getMoney()).
                            build();
                    return transactionDtoAfterSave;
                }
            }
        }catch (SQLException e){
            log.error("Cannot create transaction",e);
            throw new DaoException("Cannot create transaction",e);
        }
        log.error("Cannot create transaction");
        throw new DaoException("Cannot create transaction");
    }

    @Override
    public Optional<TransactionDto> findById(Long id) {
        return Optional.empty();
    }
}
