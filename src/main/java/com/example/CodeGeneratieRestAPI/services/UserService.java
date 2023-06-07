package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.dtos.RegisterRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.HashedPassword;
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
    private UserRepository userRepository;
    @Autowired
    private LoginRepository loginRepository;

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public User getMe(String bearerToken) {
        String token = bearerToken.substring(7);
        long id = tokenProvider.getUserIdFromJWT(token);
        return userRepository.findById(id).get();
    }

    public User add(User user) {
        user.setCreatedAt(this.CreationDate());
        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            throw new NotYetImplementedException("Username already exists");
        }
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

        if (!user.getHashedPassword().validatePassword(request.getPassword())) {
            throw new NotYetImplementedException("Password incorrect");
        }

        String token = tokenProvider.createToken(user.getId(), user.getUsername(), user.getUserType());

        UserResponseDTO userResponseDTO = new UserResponseDTO(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getUserType(), user.getCreatedAt());

        return new LoginResponseDTO(token, userResponseDTO);
    }

    public Enum<UserType> validate(String bearerToken) {
        String token = bearerToken.substring(7);
        if (tokenProvider.validateToken(token)) {
            String userType = tokenProvider.getUserTypeFromJWT(token);
            return UserType.valueOf(userType);
        } else {
            return null;
        }
    }

    public LoginResponseDTO register(RegisterRequestDTO request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(new HashedPassword(request.getPassword()));
        user.setUserType(UserType.USER);
        user.setCreatedAt(this.CreationDate());
        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            throw new NotYetImplementedException("Username already exists");
        }
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new NotYetImplementedException("Email already exists");
        }
        userRepository.save(user);

        // Login after register
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername(user.getUsername());
        loginRequestDTO.setPassword(request.getPassword());
        return login(loginRequestDTO);
    }
}