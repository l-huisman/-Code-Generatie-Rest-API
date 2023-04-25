package com.example.CodeGeneratieRestAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"users\"")
public class User extends BaseEntity {
    private int id;
    private String first_name;
    private String last_name;
    private String username;
    private String password;
    private String email;
    private String created_at;
}
