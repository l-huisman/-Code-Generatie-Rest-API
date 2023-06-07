package com.example.CodeGeneratieRestAPI.dtos;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
}
