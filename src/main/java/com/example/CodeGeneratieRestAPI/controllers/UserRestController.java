package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"users", "users/"})
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }
}
