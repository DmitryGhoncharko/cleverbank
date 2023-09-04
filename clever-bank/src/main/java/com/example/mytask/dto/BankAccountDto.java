package com.example.mytask.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@Data
@RequiredArgsConstructor
public class BankAccountDto {
    private final Long bankAccountId;

    private final UserDto userDto;

    private final Timestamp dateCreated;

    private final CurrencyDto currencyDto;

    private final double balance;

    private final BankDto bankDto;

    private final Timestamp accrualDate;
}
