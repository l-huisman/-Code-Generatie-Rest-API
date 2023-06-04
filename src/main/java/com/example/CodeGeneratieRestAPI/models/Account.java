package com.example.CodeGeneratieRestAPI.models;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"accounts\"")

public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;
    @Column(unique = true)
    private String iban;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = true)
    private User user;
    // The User object can optionally be filled, but the username is always filled
    @Column(name = "USER_ID", nullable = true, insertable = false, updatable = false)
    private Long userId;
    private String name;
    private Float dailyLimit;
    private Float transactionLimit;
    private Float absoluteLimit;
    private Float balance;
    private Boolean isSavings;
    private Date createdAt;
    private Boolean isActive;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fromAccount")
    private List<Transaction> sentTransactions;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fromAccount")
    private List<Transaction> receivedTransactions;

    // A constructor for Account that takes an AccountRequestDTO
    public Account(AccountRequestDTO accountRequestDTO) {
        this.userId = accountRequestDTO.getUserId();
        this.iban = accountRequestDTO.getIban();
        this.name = accountRequestDTO.getAccountName();
        this.dailyLimit = accountRequestDTO.getDailyLimit();
        this.transactionLimit = accountRequestDTO.getTransactionLimit();
        this.absoluteLimit = accountRequestDTO.getAbsoluteLimit();
        this.balance = accountRequestDTO.getBalance();
        this.isSavings = accountRequestDTO.getIsSavings();
        this.isActive = accountRequestDTO.getIsActive();
    }

    // A constructor for Account that takes an AccountRequestDTO and a User
    public Account(AccountRequestDTO accountRequestDTO, User user) {
        this.userId = accountRequestDTO.getUserId();
        this.iban = accountRequestDTO.getIban();
        this.name = accountRequestDTO.getAccountName();
        this.dailyLimit = accountRequestDTO.getDailyLimit();
        this.transactionLimit = accountRequestDTO.getTransactionLimit();
        this.absoluteLimit = accountRequestDTO.getAbsoluteLimit();
        this.balance = accountRequestDTO.getBalance();
        this.isSavings = accountRequestDTO.getIsSavings();
        this.isActive = accountRequestDTO.getIsActive();
        this.user = user;
    }

    public Long getUserId() {
        if (user != null) {
            return user.getId();
        }
        return userId;
    }

    public void setUsername(Long userId) {
        this.userId = userId;
    }

    public void setIban(String iban) {
        if (this.iban == null || this.iban.isEmpty()) {
            this.iban = iban;
        } else {
            throw new IllegalStateException("Iban is already set");
        }
    }

    public Float updateBalance(Float amount) {
        try {
            if (this.balance == null) {
                throw new IllegalStateException("Account balance is not set");
            }
            if (checkIfAmountIsHigherThanLimits(amount)) {
                this.balance = this.balance + amount;
            }

            return this.balance;
        } catch (ParseException e) {
            throw new IllegalStateException("Invalid date format");
        } catch (Exception e) {
            throw e;
        }

    }

    public Account update(Account account) {
        this.iban = account.getIban();
        this.name = account.getName();
        this.dailyLimit = account.getDailyLimit();
        this.transactionLimit = account.getTransactionLimit();
        this.absoluteLimit = account.getAbsoluteLimit();
        this.balance = account.getBalance();
        this.isSavings = account.getIsSavings();
        this.isActive = account.getIsActive();
        this.createdAt = account.getCreatedAt();
        return this;
    }

    //  This is technically duplicate code, as this is also already done in the TransactionService class
    //  However, this is done to make sure that the Account class is self-sufficient
    private boolean checkIfAmountIsHigherThanLimits(Float amount) throws ParseException {
        // Check if the amount is higher than the transaction limit
        if (amount > this.transactionLimit) {
            throw new IllegalArgumentException("Transaction amount exceeds the transaction limit, the limit is: " + this.transactionLimit);
        }
        // Check if the amount is higher than or exceeds the daily limit (including the amount already spent today)
        if (amount > this.dailyLimit || (getAmountSpentToday() + amount) > this.dailyLimit) {
            throw new IllegalArgumentException("Transaction amount exceeds the daily limit, the limit is: " + this.dailyLimit);
        }
        // Check if the amount change exceeds the absolute limit
        if ((this.balance - amount) < this.absoluteLimit) {
            throw new IllegalArgumentException("Transaction amount exceeds the absolute limit, the limit is: " + this.absoluteLimit + " the current balance is: " + this.balance);
        }
        return true;
    }

    private Float getAmountSpentToday() throws ParseException {
        List<Transaction> allTransactions = this.getAllTransactions();

        Float amountSpentToday = 0.0f;

        // Create a date format to compare the dates
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Get the current date
        Date currentDate = new Date();

        // Loop through all the transactions and check if the date is the same as the current date
        for (Transaction transaction : allTransactions) {
            if (currentDate.compareTo(transaction.getCreatedAt()) == 0) {
                amountSpentToday += transaction.getAmount();
            }
        }
        return amountSpentToday;
    }

    private List<Transaction> getAllTransactions() {
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(this.sentTransactions);
        allTransactions.addAll(this.receivedTransactions);
        return allTransactions;
    }
}