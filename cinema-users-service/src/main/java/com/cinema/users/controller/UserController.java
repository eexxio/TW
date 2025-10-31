package com.cinema.users.controller;

import com.cinema.users.dto.UserCreateDTO;
import com.cinema.users.dto.UserDTO;
import org.apache.coyote.Response;
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
    public ResponseEntity<UserDTO>createUser(@RequestBody UserCreateDTO userCreateDTO) {
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

}
