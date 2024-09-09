package com.example.test.mapper;

import com.example.test.dto.UserDTO;
import com.example.test.model.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(Users user);

    Users toUser(UserDTO userDTO);
}
