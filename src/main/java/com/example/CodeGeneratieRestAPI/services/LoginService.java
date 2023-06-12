package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.exceptions.InvalidTokenException;
import com.example.CodeGeneratieRestAPI.exceptions.PasswordValidationException;
import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    JwTokenProvider tokenProvider;

    @Autowired
    LoginRepository loginRepository;

    @Autowired
    JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = loginRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getHashedPassword().validatePassword(request.getPassword())) {
            throw new PasswordValidationException("Password is incorrect");
        }
        String token = tokenProvider.createToken(user.getId(), user.getUsername(), user.getUserType());
        UserResponseDTO userResponseDTO = new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getEmail(), user.getUserType());
        return new LoginResponseDTO(token, userResponseDTO);
    }

    public Enum<UserType> validate(String bearerToken) {
        if (jwtService.validateJwtToken(bearerToken)) {
            return jwtService.getUserTypeFromJwtToken(bearerToken);
        } else {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
