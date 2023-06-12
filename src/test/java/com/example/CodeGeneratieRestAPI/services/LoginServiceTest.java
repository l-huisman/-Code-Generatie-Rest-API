package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.exceptions.InvalidTokenException;
import com.example.CodeGeneratieRestAPI.exceptions.PasswordValidationException;
import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.HashedPassword;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.LoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LoginServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private JwTokenProvider tokenProvider;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private LoginRequestDTO getMockLoginRequestDTO() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("username");
        loginRequestDTO.setPassword("password");
        return loginRequestDTO;
    }

    private LoginResponseDTO getMockLoginResponseDTO() {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken("token");
        return loginResponseDTO;
    }

    private User getMockUser() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setUsername("username");
        mockUser.setEmail("email");
        mockUser.setPassword(new HashedPassword("password"));
        mockUser.setUserType(UserType.USER);
        return mockUser;
    }

    @Test
    void testLoginWithValidCredentials() {
        // Arrange
        LoginRequestDTO request = getMockLoginRequestDTO();
        User user = getMockUser();
        when(loginRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(tokenProvider.createToken(user.getId(), user.getUsername(), user.getUserType())).thenReturn("token");

        // Act
        LoginResponseDTO response = loginService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertNotNull(response.getUser());
        assertEquals(user.getId(), response.getUser().getId());
        assertEquals(user.getFirstName(), response.getUser().getFirstName());
        assertEquals(user.getLastName(), response.getUser().getLastName());
        assertEquals(user.getUsername(), response.getUser().getUsername());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        assertEquals(user.getUserType(), response.getUser().getUserType());
    }

    @Test
    void testLoginWithInvalidCredentials() {
        // Arrange
        LoginRequestDTO request = getMockLoginRequestDTO();
        User user = getMockUser();
        user.setPassword(new HashedPassword("wrong_password"));
        when(loginRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(PasswordValidationException.class, () -> loginService.login(request));
    }

    @Test
    void testLoginWithNonExistingUser() {
        // Arrange
        LoginRequestDTO request = getMockLoginRequestDTO();
        when(loginRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> loginService.login(request));
    }

    @Test
    void testValidateWithValidToken() {
        // Arrange
        String token = "valid_token";
        when(jwtService.validateJwtToken(token)).thenReturn(true);
        when(jwtService.getUserTypeFromJwtToken(token)).thenReturn(UserType.USER);

        // Act
        Enum<UserType> userType = loginService.validate(token);

        // Assert
        assertNotNull(userType);
        assertEquals(UserType.USER, userType);
    }

    @Test
    void testValidateWithInvalidToken() {
        // Arrange
        String token = "invalid_token";
        when(jwtService.validateJwtToken(token)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> loginService.validate(token));
    }
}