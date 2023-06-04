package com.example.CodeGeneratieRestAPI.dtos;

import com.example.CodeGeneratieRestAPI.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private User user;
}
