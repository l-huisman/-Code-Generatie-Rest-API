package com.example.CodeGeneratieRestAPI.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.List;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"users\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;

    private String first_name;
    private String last_name;
    private String username;
    private HashedPassword password;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "userType")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<UserType> userType;

    private String created_at;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Account> accounts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Transaction> transactions;

    public String getPassword() {
        return new String(Base64.getDecoder().decode(this.hash));
    }

    public boolean validatePassword(String password) {
        HashedPassword hashedPassword = new HashedPassword(password);
        return hashedPassword.validatePassword(password);
    }
}
