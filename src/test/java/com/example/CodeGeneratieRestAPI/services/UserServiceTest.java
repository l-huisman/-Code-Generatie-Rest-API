// BEGIN: 5c8f7a6d7d7d
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private JwTokenProvider tokenProvider;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAll() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setUserType(UserType.USER);
        user1.setCreatedAt("2021-10-01");
        userList.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setUserType(UserType.EMPLOYEE);
        user2.setCreatedAt("2021-10-02");
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
    }

    @Test
    public void testGetMe() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setUserType(UserType.USER);
        user.setCreatedAt("2021-10-01");

        String token = "token123";
        when(jwtService.getUserIdFromJwtToken(token)).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getMe("Bearer " + token);

        assertEquals(user, result);
    }

    @Test
    public void testAdd() {
        User user = new User();
        user.setUsername("user1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setUserType(UserType.USER);
        user.setCreatedAt("2021-10-01");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.add(user);

        assertEquals(user, result);
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setUserType(UserType.USER);
        user.setCreatedAt("2021-10-01");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.update(user);

        assertEquals(user, result);
    }

    @Test
    public void testGetById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setUserType(UserType.USER);
        user.setCreatedAt("2021-10-01");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertEquals(user, result);
    }

    @Test
    public void testDelete() {
        userService.delete(1L);
    }

    @Test
    public void testLogin() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("user1");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setUserType(UserType.USER);
        user.setCreatedAt("2021-10-01");

        when(loginRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(tokenProvider.createToken(1L, "user1", UserType.USER)).thenReturn("token123");

        UserResponseDTO userResponseDTO = new UserResponseDTO(user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getEmail(), user.getUserType(), user.getCreatedAt());

        LoginResponseDTO expected = new LoginResponseDTO("token123", userResponseDTO);

        LoginResponseDTO result = userService.login(request);

        assertEquals(expected, result);
    }

    @Test
    public void testValidate() {
        String token = "token123";
        when(jwtService.validateJwtToken(token)).thenReturn(true);
        when(jwtService.getUserTypeFromJwtToken(token)).thenReturn(UserType.USER);

        Enum<UserType> result = userService.validate("Bearer " + token);

        assertEquals(UserType.USER, result);
    }
}
// END: 5c8f7a6d7d7d