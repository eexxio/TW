package com.cinema.users.mapper;

import com.cinema.users.dto.UserCreateDTO;
import com.cinema.users.dto.UserDTO;
import com.cinema.users.entity.User;

public class UserMapper {
    public static User toEntity(UserCreateDTO userCreateDTO) {
        return new User(
                userCreateDTO.getFirstName(),
                userCreateDTO.getLastName(),
                userCreateDTO.getEmail(),
                userCreateDTO.getPassword(),
                userCreateDTO.getPhoneNumber(),
                userCreateDTO.getDateOfBirth(),
                userCreateDTO.getRole()

        );
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getPhoneNumber(),
                user.getDateOfBirth()
        );
    }
}
