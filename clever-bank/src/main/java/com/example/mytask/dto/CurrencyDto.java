package com.example.mytask.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
public class CurrencyDto {
    private final Long id;
    private final String name;
}
