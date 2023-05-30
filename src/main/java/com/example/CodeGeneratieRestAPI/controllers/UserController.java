package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PostMapping
    public User add(@RequestBody User user) {
        return userService.add(user);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return userService.login(request);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }
}
