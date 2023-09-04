package com.example.mytask.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class TransactionTypeDto {
    private final Long id;
    private final String name;
}
