package com.example.CodeGeneratieRestAPI.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"accounts\"")
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable=true)
    private User user;

    private String iban;
    private String name;
    private Float daily_limit;
    private Float transaction_limit;
    private Float absolute_limit;
    private Float balance;
    private Boolean is_savings;
    private String created_at;
    private Boolean is_active;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="Account")
    private List<Transaction> transactions ;
}
