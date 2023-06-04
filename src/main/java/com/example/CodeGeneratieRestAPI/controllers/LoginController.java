package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    UserService userService;

    @PostMapping
    public LoginResponseDTO login(@RequestBody LoginRequestDTO req) {
        return userService.login(req);
    }

    @GetMapping("/validate")
    public Enum<UserType> validate(@RequestHeader("Authorization") String token) {
        return userService.validate(token);
    }
}
