package com.example.CodeGeneratieRestAPI.dtos;

import com.example.CodeGeneratieRestAPI.models.UserType;
import lombok.Data;

@Data
public class UserResponseDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private UserType userType;
    private String createdAt;
}
