package com.cinema.users.service;

import com.cinema.users.dto.UserCreateDTO;
import com.cinema.users.dto.UserDTO;

import java.util.List;

public interface IUserService {
    UserDTO createUser(UserCreateDTO userDTO);
    UserDTO updateUser(String email, UserCreateDTO userDTO);
    List<UserDTO> getAllUsers();
    boolean deleteUser(String email);
    boolean login(String email, String password);
    List<UserDTO> searchByName(String keyword);
    List<UserDTO> filterByRole(String role);
    List<UserDTO> sortByDateOfBirth(String direction);


}
