package com.example.mytask.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@RequiredArgsConstructor
public class TransactionDto {
    private final Long id;
    private final BankAccountDto bankAccountDtoTo;
    private final TransactionTypeDto transactionTypeDto;
    private final Timestamp transactionDate;
    private final BankAccountDto bankAccountDtoFrom;
    private final double money;
}
