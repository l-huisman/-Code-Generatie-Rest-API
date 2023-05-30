package com.example.CodeGeneratieRestAPI.dtos;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}
