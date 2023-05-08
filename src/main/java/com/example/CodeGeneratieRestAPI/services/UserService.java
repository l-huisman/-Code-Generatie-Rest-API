package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public User add(User user) {
        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public User getById(long id) {
        return userRepository.findById(id).get();
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

    public User login(User user) {
        return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
    }
}