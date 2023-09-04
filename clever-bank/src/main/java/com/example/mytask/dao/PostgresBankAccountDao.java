package com.example.mytask.dao;

import com.example.mytask.dto.BankAccountDto;
import com.example.mytask.dto.BankDto;
import com.example.mytask.dto.CurrencyDto;
import com.example.mytask.dto.UserDto;
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
public class PostgresBankAccountDao implements BankAccountDao {
    private static final String SQL_CREATE_BANK_ACCOUNT = "insert into bank_account(bank_account_user_id, date_created, currency_id, balance, bank_id) values (?,?,?,?,?)";
    private static final String SQL_FIND_BY_ID = "select bank_account_id,bank_account_user_id, u.user_first_name, u.user_last_name, u.user_family_name,currency_id,c.currency_name,  balance,bank_id, b.bank_name, accrual_date from bank_account " + "left join _user u on u.user_id = bank_account.bank_account_user_id " + "left join _currency c on c.currency_id = bank_account.currency_id " + "left outer join bank b on b.bank_id = bank_account.bank_id";
    private static final String SQL_UPDATE = "update bank_account set bank_account_user_id = ?, date_created = ?, currency_id = ?, balance = ?, bank_id = ?, accrual_date = ? where bank_account_id = ?";
    private final Connection connection;


    @Override
    public BankAccountDto createBankAccount(BankAccountDto bankAccountDto) throws DaoException {
        try {
            Timestamp timestamp = new Timestamp(new Date().getTime());
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_BANK_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, bankAccountDto.getUserDto().getId());
            preparedStatement.setTimestamp(2, timestamp);
            preparedStatement.setLong(3, bankAccountDto.getCurrencyDto().getId());
            preparedStatement.setDouble(4, bankAccountDto.getBalance());
            preparedStatement.setLong(5, bankAccountDto.getBankDto().getId());
            int countRowsCreated = preparedStatement.executeUpdate();
            if (countRowsCreated > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    BankAccountDto bankAccountDtoForReturn = BankAccountDto.builder()
                            .bankAccountId(resultSet.getLong(1))
                            .userDto(bankAccountDto.getUserDto())
                            .dateCreated(timestamp)
                            .currencyDto(bankAccountDto.getCurrencyDto())
                            .balance(bankAccountDto.getBalance())
                            .bankDto(bankAccountDto.getBankDto())
                            .accrualDate(timestamp)
                            .build();
                    return bankAccountDtoForReturn;
                }
            }
        } catch (SQLException e) {
            log.error("Cannot create bank account ", e);
            throw new DaoException("Cannot create bank account ", e);
        }
        log.error("Cannot create bank account");
        throw new DaoException("Cannot create bank account");
    }

    @Override
    public Optional<BankAccountDto> findById(Long id) throws DaoException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                BankAccountDto bankAccountDto = BankAccountDto.builder().
                        bankAccountId(resultSet.getLong(1)).
                        userDto(UserDto.builder().
                                id(resultSet.getLong(2)).
                                firstName(resultSet.getString(3)).
                                lastName(resultSet.getString(4)).
                                familyName(resultSet.getString(5)).
                                build()).
                        currencyDto(CurrencyDto.builder().
                                id(resultSet.getLong(6)).
                                name(resultSet.getString(7)).
                                build()).
                        balance(resultSet.getDouble(8)).
                        bankDto(BankDto.builder().
                                id(resultSet.getLong(9)).
                                name(resultSet.getString(10)).
                                build()).
                        accrualDate(resultSet.getTimestamp(11)).
                        build();
                return Optional.of(bankAccountDto);
            }
        } catch (SQLException e) {
            log.error("Cannot find by id",e);
            throw new DaoException("Cannot find by id",e);
        }
        return Optional.empty();
    }

    @Override
    public BankAccountDto update(BankAccountDto bankAccountDto) throws DaoException {
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE);
                preparedStatement.setLong(1,bankAccountDto.getUserDto().getId());
                preparedStatement.setTimestamp(2,bankAccountDto.getDateCreated());
                preparedStatement.setLong(3,bankAccountDto.getCurrencyDto().getId());
                preparedStatement.setDouble(4,bankAccountDto.getBalance());
                preparedStatement.setLong(5,bankAccountDto.getBankDto().getId());
                preparedStatement.setLong(6,bankAccountDto.getBankAccountId());
                preparedStatement.setTimestamp(7,bankAccountDto.getAccrualDate());
                preparedStatement.executeUpdate();
                return bankAccountDto;
            }catch (SQLException e){
                log.error("Cannot update",e);
                throw new DaoException("Cannot update",e);
            }
    }
}
