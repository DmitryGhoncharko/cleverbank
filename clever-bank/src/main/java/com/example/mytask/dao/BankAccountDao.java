package com.example.mytask.dao;

import com.example.mytask.dto.BankAccountDto;
import com.example.mytask.dto.BankDto;
import com.example.mytask.exception.DaoException;

import java.util.Optional;

public interface BankAccountDao {
    BankAccountDto createBankAccount(BankAccountDto bankAccountDto) throws DaoException;

    BankAccountDto update(BankAccountDto bankAccountDto) throws DaoException;

    Optional<BankAccountDto> findById(Long id) throws DaoException;
}
