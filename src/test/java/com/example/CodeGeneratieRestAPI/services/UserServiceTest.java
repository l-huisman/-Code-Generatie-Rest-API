package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.UserRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.HashedPassword;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

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

    @Test
    public void testGetAll() {
        List<User> users = new ArrayList<>();
        users.add(getMockUser());
        when(userRepository.findAll()).thenReturn(users);
        when(modelMapper.map(any(User.class), eq(UserResponseDTO.class))).thenReturn(getMockUserResponseDTO());

        List<UserResponseDTO> result = userService.getAll();

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllNoUsersFound() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserResponseDTO> result = userService.getAll();

        assertEquals(0, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetMe() {
        User user = getMockUser();
        String jwToken = jwTokenProvider.createToken(user.getId(), user.getUsername(), user.getUserType());

        when(jwtService.getUserIdFromJwtToken(jwToken)).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(getMockUserResponseDTO());

        UserResponseDTO result = userService.getMe(jwToken);

        assertEquals(getMockUserResponseDTO(), result);
        verify(jwtService, times(1)).getUserIdFromJwtToken(jwToken);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetMeNoUserFound() {
        User user = getMockUser();
        String jwToken = jwTokenProvider.createToken(user.getId(), user.getUsername(), user.getUserType());

        when(jwtService.getUserIdFromJwtToken(jwToken)).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getMe(jwToken));
        verify(jwtService, times(1)).getUserIdFromJwtToken(jwToken);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testAdd() {
        // create a user object
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setPassword(new HashedPassword("password"));
        user.setEmail("johndoe@example.com");
        user.setUserType(UserType.USER);

        // create a user request DTO object
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("John");
        userRequestDTO.setLastName("Doe");
        userRequestDTO.setUsername("johndoe");
        userRequestDTO.setPassword("password");
        userRequestDTO.setEmail("johndoe@example.com");
        userRequestDTO.setUserType(UserType.USER);

        // map the user request DTO to a user object
        when(modelMapper.map(userRequestDTO, User.class)).thenReturn(user);

        // call the add method
        userService.add(userRequestDTO);

        // verify if the save method is called with the correct user object
        verify(userRepository, times(1)).save(user);
    }

    // @Test
    // public void testUpdate() {
    // Long id = 1L;
    // UserRequestDTO userRequestDTO = new UserRequestDTO("John", "Doe", "johndoe",
    // "johndoe@example.com", "password",
    // "user");
    // User userToUpdate = new User(id, "Jane", "Doe", "janedoe",
    // "janedoe@example.com",
    // new HashedPassword("password"), "user", new Date());
    // User updatedUser = new User(id, "John", "Doe", "johndoe",
    // "johndoe@example.com", new HashedPassword("password"),
    // "user", new Date());
    // when(userRepository.findById(id)).thenReturn(Optional.of(userToUpdate));
    // when(modelMapper.map(userRequestDTO, User.class)).thenReturn(updatedUser);
    // when(userRepository.save(updatedUser)).thenReturn(updatedUser);
    // when(modelMapper.map(updatedUser, UserResponseDTO.class))
    // .thenReturn(new UserResponseDTO(id, "John", "Doe", "johndoe",
    // "johndoe@example.com", "user"));
    //
    // UserResponseDTO result = userService.update(id, userRequestDTO);
    //
    // assertEquals(updatedUser.getId(), result.getId());
    // verify(userRepository, times(1)).save(updatedUser);
    // }
    //
    // @Test
    // public void testUpdateNoUserFound() {
    // Long id = 1L;
    // UserRequestDTO userRequestDTO = new UserRequestDTO("John", "Doe", "johndoe",
    // "johndoe@example.com", "password",
    // "user");
    // when(userRepository.findById(id)).thenReturn(Optional.empty());
    //
    // assertThrows(UserNotFoundException.class, () -> userService.update(id,
    // userRequestDTO));
    // verify(userRepository, times(0)).save(any(User.class));
    // }
    //
    @Test
    public void testGetById() {
        Long id = 1L;
        User user = getMockUser();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(getMockUserResponseDTO());

        UserResponseDTO result = userService.getById(id);

        assertEquals(getMockUserResponseDTO(), result);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void testGetByIdNoUserFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void testDelete() {
        Long id = 1L;

        userService.delete(id);

        verify(userRepository, times(1)).deleteById(id);
    }

}