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
        userResponseDTO.setId(1L);
        userResponseDTO.setFirstName("John");
        userResponseDTO.setLastName("Doe");
        userResponseDTO.setUsername("johndoe");
        userResponseDTO.setEmail("johndoe@example.com");
        userResponseDTO.setUserType(UserType.USER);
        return userResponseDTO;
    }

    @Test
    public void testGetAll() {
        List<User> users = new ArrayList<>();
        users.add(getMockUser());
        when(userRepository.findAll()).thenReturn(users);
        when(modelMapper.map(any(User.class), eq(UserResponseDTO.class))).thenReturn(getMockUserResponseDTO());

        List<UserResponseDTO> result = userService.getAll(false);

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllNoUsersFound() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserResponseDTO> result = userService.getAll(false);

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

    //TODO:Fix this test

    // @Test
    // public void testAdd() {
    //     User user = getMockUser();
    //     UserRequestDTO userRequestDTO = getMockUserRequestDTO();

    //     // when(modelMapper.map(userRequestDTO, User.class)).thenReturn(user);
    //     when(userRepository.save(user)).thenReturn(user);
    //     // when(modelMapper.map(user,
    //             UserResponseDTO.class)).thenReturn(getMockUserResponseDTO());

    //     UserResponseDTO result = userService.add(userRequestDTO);

    //     assertEquals(getMockUserResponseDTO(), result);
    //     // verify(modelMapper, times(1)).map(userRequestDTO, User.class);
    //     verify(userRepository, times(1)).save(user);
    //     // verify(modelMapper, times(1)).map(user, UserResponseDTO.class);
    // }

    @Test
    public void testUpdate() {
        Long id = 1L;
        User user = getMockUser();
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        // when(modelMapper.map(userRequestDTO, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        // when(modelMapper.map(user,
        // UserResponseDTO.class)).thenReturn(getMockUserResponseDTO());

        UserResponseDTO result = userService.update(id, userRequestDTO);

        assertEquals(getMockUserResponseDTO(), result);
        verify(userRepository, times(1)).findById(id);
        // verify(modelMapper, times(1)).map(userRequestDTO, User.class);
        verify(userRepository, times(1)).save(user);
        // verify(modelMapper, times(1)).map(user, UserResponseDTO.class);

    }

    @Test
    public void testUpdateNoUserFound() {
        Long id = 1L;
        UserRequestDTO userRequestDTO = getMockUserRequestDTO();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(id, userRequestDTO));
        verify(userRepository, times(1)).findById(id);
    }

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