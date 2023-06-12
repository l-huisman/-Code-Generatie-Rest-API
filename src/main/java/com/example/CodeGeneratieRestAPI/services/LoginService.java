package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.LoginRepository;
import org.hibernate.cfg.NotYetImplementedException;
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
        return loginRepository.findByUsername(request.getUsername())
                .map(user -> {
                    if (user.getPassword().equals(request.getPassword())) {
                        throw new NotYetImplementedException("Password incorrect");
                    }
                    String token = tokenProvider.createToken(user.getId(), user.getUsername(), user.getUserType());
                    UserResponseDTO userResponseDTO = new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(),
                            user.getUsername(), user.getEmail(), user.getUserType());
                    return new LoginResponseDTO(token, userResponseDTO);
                })
                .orElseThrow(() -> new NotYetImplementedException("User not found"));
    }

    public Enum<UserType> validate(String bearerToken) {
        if (jwtService.validateJwtToken(bearerToken)) {
            return jwtService.getUserTypeFromJwtToken(bearerToken);
        } else {
            return null;
        }
    }
}
