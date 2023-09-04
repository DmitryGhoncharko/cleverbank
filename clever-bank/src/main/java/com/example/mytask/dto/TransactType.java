package com.example.mytask.dto;

public enum TransactType {
    NONE(""),ADD_BALANCE("Пополнение счета"), WITHDRAW("Снятие со счета"), TRANSACTION("Перевод");
    private String name;
    TransactType(String name) {
        this.name = name;
    }
}
