package com.example.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.test.dto.UserDTO;
import com.example.test.mapper.UserMapper;
import com.example.test.model.Users;
import com.example.test.repository.UserRepository;
import com.example.test.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUser_ShouldReturnSavedUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        userDTO.setSex("Male");

        Users user = new Users();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setSex("Male");

        when(userMapper.toUser(userDTO)).thenReturn(user);
        when(userRepository.save(any(Users.class))).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO savedUserDTO = userService.addUser(userDTO);

        assertThat(savedUserDTO.getFirstName()).isEqualTo("John");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        Users user = new Users();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO foundUserDTO = userService.getUserById(1L);

        assertThat(foundUserDTO.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(1L);
        });

        assertThat(exception.getMessage()).contains("User not found with id: 1");
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        Users user1 = new Users();
        user1.setId(1L);
        user1.setFirstName("John");

        Users user2 = new Users();
        user2.setId(2L);
        user2.setFirstName("Jane");

        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setFirstName("John");

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setFirstName("Jane");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(userMapper.toUserDTO(user1)).thenReturn(userDTO1);
        when(userMapper.toUserDTO(user2)).thenReturn(userDTO2);

        List<UserDTO> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        verify(userRepository, times(1)).findAll();
    }
}
