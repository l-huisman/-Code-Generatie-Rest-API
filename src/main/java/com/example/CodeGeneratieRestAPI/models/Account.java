package com.example.CodeGeneratieRestAPI.models;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.interfaces.IRepositoryModel;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.Id;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"accounts\"")
public class Account extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String iban;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable=true)
    private User user;
    // The User object can optionally be filled, but the userId is always filled
    private Integer userId;
    public Integer getUserId() {
        if (user != null) {
            return user.getId();
        }
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private String name;
    private Float dailyLimit;
    private Float transactionLimit;
    private Float absoluteLimit;
    private Float balance;
    private Boolean isSavings;
    private String createdAt;
    private Boolean isActive;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="Account")
    private List<Transaction> transactions ;

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
}
