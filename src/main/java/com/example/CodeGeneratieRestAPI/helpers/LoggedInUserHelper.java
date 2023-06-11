package com.example.CodeGeneratieRestAPI.helpers;

import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class LoggedInUserHelper {
    @Autowired
    private final UserService userService;

    public LoggedInUserHelper() {
        this.userService = new UserService();
    }

    //  Get the current logged-in user
    public User getLoggedInUser() {
        return userService.getLoggedInUser();
    }
}
