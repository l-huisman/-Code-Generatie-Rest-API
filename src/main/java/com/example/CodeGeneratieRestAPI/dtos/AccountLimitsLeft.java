package com.example.CodeGeneratieRestAPI.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Class representing the JSON object
@Data
public class AccountLimitsLeft {
    @JsonProperty("Daily limit left")
    private float dailyLimitLeft;

    @JsonProperty("Transaction Limit")
    private float transactionLimit;

    @JsonProperty("Amount spendable on next transaction")
    private float amountSpendableOnNextTransaction;

    @JsonProperty("Difference balance and absolute limit")
    private float differenceBalanceAndAbsoluteLimit;


}