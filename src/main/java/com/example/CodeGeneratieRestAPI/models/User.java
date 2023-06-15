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
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
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

    private String createdAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Account> accounts;

    @OneToMany( mappedBy = "user")
    private List<Transaction> transactions;

    public String getPassword() {
        return password.getPassword();
    }

    public HashedPassword getHashedPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName +
                ", lastName='" + lastName +
                ", username='" + username +
                ", password='" + password +
                ", email='" + email +
                ", userType='" + userType +
                ", createdAt='" + createdAt +
                '}';
    }
}