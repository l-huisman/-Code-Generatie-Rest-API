package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.UserRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.exceptions.UserAlreadyExistsException;
import com.example.CodeGeneratieRestAPI.exceptions.UserDeletionException;
import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
import com.example.CodeGeneratieRestAPI.exceptions.UserUpdateException;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.ApiResponse;
import com.example.CodeGeneratieRestAPI.models.HashedPassword;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import com.example.CodeGeneratieRestAPI.services.JwtService;
import com.example.CodeGeneratieRestAPI.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private JwTokenProvider jwTokenProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setEmail("johndoe@example.com");
        user.setPassword(new HashedPassword("password"));
        user.setUserType(UserType.USER);
        user.setCreatedAt(new Date().toString());
        return user;
    }

    private UserRequestDTO getMockUserRequestDTO() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("John");
        userRequestDTO.setLastName("Doe");
        userRequestDTO.setUsername("johndoe");
        userRequestDTO.setEmail("johndoe@example.com");
        userRequestDTO.setPassword("password");
        userRequestDTO.setUserType(UserType.USER);
        return userRequestDTO;
    }

    private UserResponseDTO getMockUserResponseDTO() {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setFirstName("John");
        userResponseDTO.setLastName("Doe");
        userResponseDTO.setUsername("johndoe");
        userResponseDTO.setEmail("johndoe@example.com");
        userResponseDTO.setUserType(UserType.USER);
        return userResponseDTO;
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAll() throws UserNotFoundException {
        List<UserResponseDTO> users = new ArrayList<>();
        users.add(getMockUserResponseDTO());

        when(userService.getAll(false)).thenReturn(users);

        ResponseEntity<ApiResponse<List<UserResponseDTO>>> response = userController.getAll(false);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("Users found!", response.getBody().getMessage());
        assertEquals(users, response.getBody().getData());
    }

    @Test
    public void testGetById() throws UserNotFoundException {
        UserResponseDTO user = getMockUserResponseDTO();
        when(userService.getById(1L)).thenReturn(user);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getById(1L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User found!", response.getBody().getMessage());
        assertEquals(user, response.getBody().getData());
    }

    @Test
    public void testGetMe() throws UserNotFoundException {
        UserResponseDTO user = getMockUserResponseDTO();
        when(userService.getMe("token")).thenReturn(user);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getMe("token");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User found!", response.getBody().getMessage());
        assertEquals(user, response.getBody().getData());
    }

    @Test
    public void testAdd() throws UserAlreadyExistsException, UserUpdateException {
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        UserResponseDTO userResponseDTO = getMockUserResponseDTO();
        when(userService.add(userRequestDTO)).thenReturn(userResponseDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.add(userRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User created!", response.getBody().getMessage());
        assertEquals(userResponseDTO, response.getBody().getData());
    }

    @Test
    public void testUpdate() throws UserNotFoundException, UserUpdateException {
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        UserResponseDTO userResponseDTO = getMockUserResponseDTO();
        when(userService.update(1L, userRequestDTO)).thenReturn(userResponseDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.update(1L, userRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User updated!", response.getBody().getMessage());
        assertEquals(userResponseDTO, response.getBody().getData());
    }

    @Test
    public void testDelete() throws UserDeletionException {
        doNothing().when(userService).delete(1L);

        ResponseEntity<ApiResponse<Void>> response = userController.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User deleted!", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testGetAllUserNotFoundException() throws UserNotFoundException {
        when(userService.getAll(false)).thenThrow(new UserNotFoundException("No users found"));

        ResponseEntity<ApiResponse<List<UserResponseDTO>>> response = userController.getAll(false);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("No users found", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testGetByIdUserNotFoundException() throws UserNotFoundException {
        when(userService.getById(1L)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testGetMeUserNotFoundException() throws UserNotFoundException {
        when(userService.getMe("token")).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getMe("token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testAddUserAlreadyExistsException() throws UserAlreadyExistsException, UserUpdateException {
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        when(userService.add(userRequestDTO)).thenThrow(new UserAlreadyExistsException("User already exists"));

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.add(userRequestDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("User already exists", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testAddUserUpdateException() throws UserAlreadyExistsException, UserUpdateException {
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        when(userService.add(userRequestDTO)).thenThrow(new UserUpdateException("User update failed"));

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.add(userRequestDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("User update failed", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testUpdateUserNotFoundException() throws UserNotFoundException, UserUpdateException {
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        when(userService.update(1L, userRequestDTO)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.update(1L, userRequestDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testUpdateUserUpdateException() throws UserNotFoundException, UserUpdateException {
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        when(userService.update(1L, userRequestDTO)).thenThrow(new UserUpdateException("User update failed"));

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.update(1L, userRequestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("User update failed", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testDeleteUserDeletionException() throws UserDeletionException {
        doThrow(new UserDeletionException("User deletion failed")).when(userService).delete(1L);

        ResponseEntity<ApiResponse<Void>> response = userController.delete(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals("User deletion failed", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());
    }
}