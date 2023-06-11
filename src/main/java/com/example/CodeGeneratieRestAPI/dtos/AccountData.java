package com.example.CodeGeneratieRestAPI.dtos;

import com.example.CodeGeneratieRestAPI.models.Account;
import lombok.Data;

@Data
public class AccountData {
    private Account account;
    private AccountLimitsLeft accountLimitsLeft;
    public AccountData(Account account, AccountLimitsLeft accountLimitsLeft) {
        this.account = account;
        this.accountLimitsLeft = accountLimitsLeft;
    }
}
