package com.example.CodeGeneratieRestAPI.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class AccountRequestDTO {
    private Long userId;
    private String iban;
    private String accountName;
    private Float dailyLimit;
    private Float transactionLimit;
    private Float absoluteLimit;
    private Float balance;
    private Boolean isSavings;
    private Boolean isActive;

    public AccountRequestDTO() {
    }

    public AccountRequestDTO(Long userId, String iban, String accountName, Float dailyLimit, Float transactionLimit, Float absoluteLimit, Float balance, Boolean isSavings, Boolean isActive) {
        this.userId = userId;
        this.accountName = accountName;
        this.dailyLimit = dailyLimit;
        this.transactionLimit = transactionLimit;
        this.absoluteLimit = absoluteLimit;
        this.balance = balance;
        this.isSavings = isSavings;
        this.isActive = isActive;
    }
}
