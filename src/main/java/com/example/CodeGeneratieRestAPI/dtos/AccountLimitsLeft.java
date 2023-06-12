package com.example.CodeGeneratieRestAPI.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Class representing the JSON object
@Data
public class AccountLimitsLeft {
    @JsonProperty("dailyLimitLeft")
    private float dailyLimitLeft;

    @JsonProperty("transactionLimit")
    private float transactionLimit;

    @JsonProperty("amountSpendableOnNextTransaction")
    private float amountSpendableOnNextTransaction;

    @JsonProperty("differenceBalanceAndAbsoluteLimit")
    private float differenceBalanceAndAbsoluteLimit;


}