package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.UserRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserResponseDTO;
import com.example.CodeGeneratieRestAPI.exceptions.*;
import com.example.CodeGeneratieRestAPI.models.ApiResponse;
import com.example.CodeGeneratieRestAPI.services.UserService;
import jakarta.annotation.Nullable;
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
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAll(@Nullable @RequestParam(required = false, defaultValue = "") Boolean hasNoAccounts) {
        try {
            return ResponseEntity.status(HttpStatus.FOUND).body(new ApiResponse<>(true, "Users found!", userService.getAll(hasNoAccounts)));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable long id) {
        try {
            return ResponseEntity.status(HttpStatus.FOUND).body(new ApiResponse<>(true, "User found!", userService.getById(id)));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getMe(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.status(HttpStatus.FOUND).body(new ApiResponse<>(true, "User found!", userService.getMe(token)));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> add(@RequestBody UserRequestDTO user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "User created!", userService.add(user)));
        } catch (UserDTOException | UserUpdateException | UserAlreadyExistsException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(@PathVariable Long id, @RequestBody UserRequestDTO user) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "User updated!", userService.update(id, user)));
        } catch (UserDTOException | UserNotFoundException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable long id) {
        try {
            userService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "User deleted!"));
        } catch (UserDeletionException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}
