package com.cinema.users.controller;

import com.cinema.users.dto.UserCreateDTO;
import com.cinema.users.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cinema.users.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    public IUserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserDTO>createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
       UserDTO userDTO = userService.createUser(userCreateDTO);
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(userDTO);
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email,
                                        @RequestBody UserCreateDTO userCreateDTO) {
        try{
            UserDTO updateUser= userService.updateUser(email, userCreateDTO);
            return ResponseEntity.ok(updateUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteAccount(@PathVariable String email) {
        boolean isDeleted = userService.deleteUser(email);
        if(isDeleted){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Deleted successfully");
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User with email " + email + " couldn't be deleted");
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users =userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        boolean isMatching = userService.login(email, password);
        if (isMatching) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Successfully logged in");
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchByName(@RequestParam String keyword) {
        List<UserDTO> users = userService.searchByName(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterByRole(@RequestParam String role) {
        List<UserDTO> users = userService.filterByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<UserDTO>> sortByDateOfBirth(@RequestParam(defaultValue = "asc") String direction) {
        List<UserDTO> users = userService.sortByDateOfBirth(direction);
        return ResponseEntity.ok(users);
    }


}
