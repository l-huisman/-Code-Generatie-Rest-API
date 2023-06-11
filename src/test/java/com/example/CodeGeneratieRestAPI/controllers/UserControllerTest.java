package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.UserRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
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
        userResponseDTO.setCreatedAt(new Date().toString());
        return userResponseDTO;
    }

    // Snippet from: UserController.java

    // @GetMapping
    // public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAll() {
    // try {
    // return ResponseEntity.status(HttpStatus.FOUND)
    // .body(new ApiResponse<>(true, "Users found!", userService.getAll()));
    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new
    // ApiResponse<>(false, e.getMessage()));
    // }
    // }

    @Test
    public void testGetAll() {
        List<UserResponseDTO> users = new ArrayList<>();
        users.add(getMockUserResponseDTO());

        when(userService.getAll()).thenReturn(users);

        ResponseEntity<ApiResponse<List<UserResponseDTO>>> response = userController.getAll();

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("Users found!", response.getBody().getMessage());
        assertEquals(users, response.getBody().getData());

        verify(userService, times(1)).getAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testGetById() {
        UserResponseDTO user = getMockUserResponseDTO();

        when(userService.getById(1L)).thenReturn(user);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getById(1L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User found!", response.getBody().getMessage());
        assertEquals(user, response.getBody().getData());

        verify(userService, times(1)).getById(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testGetMe() {
        UserResponseDTO user = getMockUserResponseDTO();

        when(userService.getMe("dummyToken")).thenReturn(user);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getMe("dummyToken");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User found!", response.getBody().getMessage());
        assertEquals(user, response.getBody().getData());

        verify(userService, times(1)).getMe("dummyToken");
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testAdd() {
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        UserResponseDTO userResponseDTO = getMockUserResponseDTO();

        when(userService.add(userRequestDTO)).thenReturn(userResponseDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.add(userRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User created!", response.getBody().getMessage());
        assertEquals(userResponseDTO, response.getBody().getData());

        verify(userService, times(1)).add(userRequestDTO);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testUpdate() {
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        UserResponseDTO userResponseDTO = getMockUserResponseDTO();

        when(userService.update(1L, userRequestDTO)).thenReturn(userResponseDTO);

        ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.update(1L, userRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User updated!", response.getBody().getMessage());
        assertEquals(userResponseDTO, response.getBody().getData());

        verify(userService, times(1)).update(1L, userRequestDTO);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testDelete() {
        doNothing().when(userService).delete(1L);

        ResponseEntity<ApiResponse<Void>> response = userController.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("User deleted!", response.getBody().getMessage());
        assertEquals(null, response.getBody().getData());

        verify(userService, times(1)).delete(1L);
        verifyNoMoreInteractions(userService);
    }
}