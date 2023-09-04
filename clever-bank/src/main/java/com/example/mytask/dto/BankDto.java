package com.example.mytask.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
public class BankDto {
    private final Long id;
    private final String name;
}
