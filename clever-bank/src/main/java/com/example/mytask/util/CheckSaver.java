package com.example.mytask.util;

import com.example.mytask.dto.TransactionDto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class CheckSaver {
    public  static void SaveCheck(TransactionDto transactionDto){
        try {

            File file = new File("/check/" +transactionDto.getId() + ".txt");


            FileWriter writer = new FileWriter(file);
            String data =  "                       Банковский чек           " + "\n" +
                    "Чек: " + transactionDto.getId() + "\n" +
                    transactionDto.getTransactionDate() + "\n" +
                    "Тип транзакции: " + transactionDto.getTransactionTypeDto().getName() + "\n" +
                    "Банк отправителя: " + transactionDto.getBankAccountDtoFrom().getBankDto().getName() + "\n" +
                    "Банк получателя:" + transactionDto.getBankAccountDtoTo().getBankDto().getName() + "\n" +
                    "Cчет отправителя:" + transactionDto.getBankAccountDtoFrom().getBankAccountId() + "\n" +
                    "Cчет получателя:" + transactionDto.getBankAccountDtoTo().getBankAccountId() + "\n" +
                    "Сумма " + transactionDto.getMoney() + " " + transactionDto.getBankAccountDtoTo().getCurrencyDto().getName();
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
