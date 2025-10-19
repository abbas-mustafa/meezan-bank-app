package com.example.forgetpassword.mbl.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequestDTO {

    @NotBlank(message = "Name is required.")
    private String name;

    // MODIFIED: Validation now expects a plain 13-digit string.
    @NotBlank(message = "CNIC is required.")
    @Pattern(regexp = "^\\d{13}$", message = "CNIC must be exactly 13 digits.")
    private String cnicNo;

    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters.")
    private String username;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;
}