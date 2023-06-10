package com.example.CodeGeneratieRestAPI.dtos;

import com.example.CodeGeneratieRestAPI.models.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private UserType userType;
    private String createdAt;
}
