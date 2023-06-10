package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.UserRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.models.ApiResponse;
import com.example.CodeGeneratieRestAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAll() {
        try {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(new ApiResponse<>(true, "Users found!", userService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable long id) {
        try {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(new ApiResponse<>(true, "User found!", userService.getById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getMe(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(new ApiResponse<>(true, "User found!", userService.getMe(token)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> add(@RequestBody UserRequestDTO user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "User created!", userService.add(user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(@PathVariable Long id, @RequestBody UserRequestDTO user) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(true, "User updated!", userService.update(id, user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable long id) {
        try {
            userService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "User deleted!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}
