package com.example.test.service;

import com.example.test.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO addUser(UserDTO userDTO);

    void deleteUser(Long id);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long userId, UserDTO userDTO);
}

