package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    JwTokenProvider tokenProvider;
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

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findUserByUsername(request.getUsername()).orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Username or password is incorrect"));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = tokenProvider.createToken(user.getUsername(), user.getUserType());
            return new LoginResponseDTO(token);
        } else {
            throw new AuthenticationCredentialsNotFoundException("Username or password is incorrect");
        }
    }
}