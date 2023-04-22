package com.example.CodeGeneratieRestAPI.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor

public class User {
    private int id;
    private String first_name;
    private String last_name;
    private String username;
    private String password;
    private String email;
    private String created_at;
}
