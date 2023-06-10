package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.LoginRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    JwTokenProvider tokenProvider;
    @Autowired
    JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginRepository loginRepository;

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public User getMe(String bearerToken) {
        Long id = jwtService.getUserIdFromJwtToken(bearerToken);
        return userRepository.findById(id).get();
    }

    public User add(User user) {
        user.setCreatedAt(this.CreationDate());
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

    private String CreationDate() {
        Date date = new Date();
        return date.toString();
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = loginRepository.findByUsername(request.getUsername()).get();
        if (user == null) {
            throw new NotYetImplementedException("User not found");
        }
        String token = tokenProvider.createToken(user.getId(), user.getUsername(), user.getUserType());
        UserResponseDTO userResponseDTO = new UserResponseDTO(user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getEmail(), user.getUserType(), user.getCreatedAt());
        return new LoginResponseDTO(token, userResponseDTO);
    }

    public Enum<UserType> validate(String bearerToken) {
        if (jwtService.validateJwtToken(bearerToken)) {
            return jwtService.getUserTypeFromJwtToken(bearerToken);
        } else {
            return null;
        }
    }
}