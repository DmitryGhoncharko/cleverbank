package com.example.mytask.service;

import com.example.mytask.dto.BankAccountDto;
import com.example.mytask.dto.TransactionDto;
import com.example.mytask.exception.ServiceException;

public interface BankAccountService {
    BankAccountDto createBankAccount(BankAccountDto bankAccountDto) throws ServiceException;

    TransactionDto addBalance(BankAccountDto bankAccountDto, double money) throws ServiceException;

    TransactionDto withdrawBalance(BankAccountDto bankAccountDto, double money) throws ServiceException;

    TransactionDto bankAccountTransaction(BankAccountDto bankAccountDtoTo, BankAccountDto bankAccountDtoFrom, double money) throws ServiceException;
}
