package com.cinema.users.service;

import com.cinema.users.dto.UserCreateDTO;
import com.cinema.users.dto.UserDTO;
import com.cinema.users.entity.User;
import jakarta.transaction.Transactional;
import com.cinema.users.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cinema.users.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Comparator;
import java.util.List;

@Service
public class UserService implements  IUserService{

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        if(userRepository.existsByEmail(userCreateDTO.getEmail()))
        {
            throw new RuntimeException("User with email "+ userCreateDTO.getEmail()+" already exists");
        }

        userCreateDTO.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        User user = UserMapper.toEntity(userCreateDTO);
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    @Override
    public UserDTO updateUser(String email, UserCreateDTO userDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found in method updateUser"));

        user.setFirstname(userDTO.getFirstName());
        user.setLastname(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public boolean deleteUser(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
        return user.getPassword().equals(password);
    }

    @Override
    public List<UserDTO> searchByName(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> (u.getFirstname() != null && u.getFirstname().toLowerCase().contains(keyword.toLowerCase())) ||
                        (u.getLastname() != null && u.getLastname().toLowerCase().contains(keyword.toLowerCase())))
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    public List<UserDTO> filterByRole(String role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase(role))
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    public List<UserDTO> sortByDateOfBirth(String direction) {
        Comparator<User> comparator = Comparator.comparing(User::getDateOfBirth);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        return userRepository.findAll().stream()
                .filter(u -> u.getDateOfBirth() != null)
                .sorted(comparator)
                .map(UserMapper::toDTO)
                .toList();
    }

}
