package com.example.CodeGeneratieRestAPI.dtos;

import lombok.Data;

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
}
