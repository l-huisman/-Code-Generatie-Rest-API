package com.example.CodeGeneratieRestAPI.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

// Class representing the JSON object
@Data
public class AccountLimitsLeft {
    @JsonProperty("Daily limit left")
    private float dailyLimitLeft;

    @JsonProperty("Total limit left")
    private float totalLimitLeft;

    @JsonProperty("Transaction Limit")
    private float transactionLimit;

}