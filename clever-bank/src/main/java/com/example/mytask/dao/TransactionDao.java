package com.example.mytask.dao;

import com.example.mytask.dto.TransactionDto;
import com.example.mytask.dto.TransactionTypeDto;
import com.example.mytask.exception.DaoException;

import java.util.Optional;

public interface TransactionDao {
    TransactionDto createTransaction(TransactionDto transactionDto) throws DaoException;

    Optional<TransactionDto> findById(Long id);
}
