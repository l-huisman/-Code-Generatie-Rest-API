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

@Table(name = "\"users\"")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    private Integer id;

    private String first_name;
    private String last_name;
    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name="userType")
    private UserType userType;

    private String created_at;

    @OneToMany(cascade= CascadeType.ALL, mappedBy="User")
    private List<Account> accounts ;

    @OneToMany(cascade= CascadeType.ALL, mappedBy="User")
    private List<Transaction> transactions ;
}
