// BEGIN: 1a2b3c4d5e6f

package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.LoginRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.LoginResponseDTO;
import com.example.CodeGeneratieRestAPI.models.ApiResponse;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.services.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
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

    @Test
    void testValidate() {
        String token = "valid_token";
        UserType userType = UserType.EMPLOYEE;

        when(loginService.validate(token)).thenReturn(userType);

        ResponseEntity<ApiResponse<Enum<UserType>>> responseEntity = loginController.validate(token);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userType, responseEntity.getBody().getData());
    }

    @Test
    void testLogin() {
        LoginRequestDTO loginRequestDTO = getMockLoginRequestDTO();
        LoginResponseDTO loginResponseDTO = getMockLoginResponseDTO();

        when(loginService.login(loginRequestDTO)).thenReturn(loginResponseDTO);

        ResponseEntity<ApiResponse<LoginResponseDTO>> responseEntity = loginController.login(loginRequestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(loginResponseDTO, responseEntity.getBody().getData());
    }
}
// END: 1a2b3c4d5e6f