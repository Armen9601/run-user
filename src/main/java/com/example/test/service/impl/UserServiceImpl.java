package com.example.test.service.impl;

import com.example.test.dto.UserDTO;
import com.example.test.exception.UserNotFoundException;
import com.example.test.mapper.UserMapper;
import com.example.test.model.Users;
import com.example.test.repository.UserRepository;
import com.example.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        Users user = userMapper.toUser(userDTO);
        user = userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        userMapper.toUser(userDTO);
        user = userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    @Override
    public void deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(user);
    }

    @Override
    public UserDTO getUserById(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }
}
