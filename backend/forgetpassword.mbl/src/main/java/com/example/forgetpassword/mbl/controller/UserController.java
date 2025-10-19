package com.example.forgetpassword.mbl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.forgetpassword.mbl.dto.ApiResponse;
import com.example.forgetpassword.mbl.dto.user.UserRegistrationRequestDTO;
import com.example.forgetpassword.mbl.dto.user.UserResponseDTO;
import com.example.forgetpassword.mbl.service.UserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
            @Valid @RequestBody UserRegistrationRequestDTO request) {
        UserResponseDTO responseDTO = userService.registerNewUser(request);
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "User registered successfully.",
                responseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> loginUser(@RequestBody UserRegistrationRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        UserResponseDTO responseDTO = userService.authenticateUser(username, password);

        ApiResponse<UserResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Login successful.",
                responseDTO
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{cnicNo}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable String cnicNo) {
        userService.deleteUser(cnicNo);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully.");
        return ResponseEntity.ok(response);
    }
}