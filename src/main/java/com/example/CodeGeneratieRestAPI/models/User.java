package com.example.CodeGeneratieRestAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"users\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String first_name;
    private String last_name;
    private String username;

    @Embedded
    @JsonIgnore
    @AttributeOverride(name = "hash", column = @Column(name = "password_hash"))
    @AttributeOverride(name = "salt", column = @Column(name = "password_salt"))
    private HashedPassword password;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    private String created_at;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Account> accounts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Transaction> transactions;


    public boolean validatePassword(String password) {
        HashedPassword hashedPassword = new HashedPassword(password);
        return hashedPassword.validatePassword(password);
    }

    public String getPassword() {
        return password.getPassword();
    }
}