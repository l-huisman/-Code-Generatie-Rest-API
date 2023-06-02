package com.example.CodeGeneratieRestAPI.dtos;

import lombok.Data;

@Data
public class AccountRequestDTO {
    private Integer userId;
    private String iban;
    private String accountName;
    private Float dailyLimit;
    private Float transactionLimit;
    private Float absoluteLimit;
    private Float balance;
    private Boolean isSavings;
    private Boolean isActive;
    private String created_at;

}
