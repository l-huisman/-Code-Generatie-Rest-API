package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    List<User> users = new ArrayList<>();

    public UserService() {
        users.add(new User(1, "Devon", "van Wichen", "devon", "devon", "650122@student.inholland.nl", "2020-01-01"));
        users.add(new User(2, "Luke", "Huisman", "luke", "luke", "684651@student.inholland.nl", "2020-01-01"));
        users.add(new User(3, "Dewi", "Cabret", "dewi", "dewi", "647824@student.inholland.nl", "2020-01-01"));
    }

    public List<User> getAll() {
        return users;
    }

    public User add(User u) {
        users.add(u);
        return u;
    }
}