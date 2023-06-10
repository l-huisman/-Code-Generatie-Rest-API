package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.UserRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
import com.example.CodeGeneratieRestAPI.models.HashedPassword;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final ModelMapper modelMapper;
    @Autowired
    JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    public UserService() {
        this.modelMapper = new ModelMapper();
        configureModelMapper();
    }

    private void configureModelMapper() {
        TypeMap<UserRequestDTO, User> typeMap = modelMapper.getTypeMap(UserRequestDTO.class, User.class);
        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(UserRequestDTO.class, User.class);
        }
        typeMap.addMappings(mapper -> mapper.skip(User::setPassword));
    }

    public List<UserResponseDTO> getAll() {
        Iterable<User> users = userRepository.findAll();
        if (users == null) {
            throw new UserNotFoundException("No users found");
        }
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();
        for (User user : users) {
            userResponseDTOs.add(modelMapper.map(user, UserResponseDTO.class));
        }
        return userResponseDTOs;
    }

    public UserResponseDTO getMe(String bearerToken) {
        Long id = jwtService.getUserIdFromJwtToken(bearerToken);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return modelMapper.map(user, UserResponseDTO.class);
        } else {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }

    public UserResponseDTO add(UserRequestDTO user) {
        User userToSave = modelMapper.map(user, User.class);
        userToSave.setPassword(new HashedPassword(user.getPassword()));
        userToSave.setCreatedAt(CreationDate());
        userRepository.save(userToSave);
        return modelMapper.map(userToSave, UserResponseDTO.class);
    }

    public UserResponseDTO update(Long id, UserRequestDTO user) {
        User userToUpdate = userRepository.findById(id).get();
        if (userToUpdate == null) {
            throw new UserNotFoundException("No user found with id: " + id);
        }
        userToUpdate = UpdateFilledFields(user, userToUpdate);
        userRepository.save(userToUpdate);
        return modelMapper.map(userToUpdate, UserResponseDTO.class);
    }

    public UserResponseDTO getById(long id) {
        User user = userRepository.findById(id).get();
        if (user == null) {
            throw new UserNotFoundException("No user found with id: " + id);
        }
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

    private String CreationDate() {
        Date date = new Date();
        return date.toString();
    }

    private User UpdateFilledFields(UserRequestDTO user, User userToUpdate) {
        Optional.ofNullable(user.getFirstName()).ifPresent(userToUpdate::setFirstName);
        Optional.ofNullable(user.getLastName()).ifPresent(userToUpdate::setLastName);
        Optional.ofNullable(user.getUsername()).ifPresent(userToUpdate::setUsername);
        Optional.ofNullable(user.getEmail()).ifPresent(userToUpdate::setEmail);
        Optional.ofNullable(user.getPassword()).map(HashedPassword::new).ifPresent(userToUpdate::setPassword);
        Optional.ofNullable(user.getUserType()).ifPresent(userToUpdate::setUserType);
        return userToUpdate;
    }

}