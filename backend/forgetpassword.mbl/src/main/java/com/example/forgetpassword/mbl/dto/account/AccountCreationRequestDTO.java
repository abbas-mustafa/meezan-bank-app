package com.example.forgetpassword.mbl.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountCreationRequestDTO {

    private String accountType;

    @NotBlank(message = "Account number is required.")
    @Pattern(regexp = "^\\d{14}$", message = "Account number must be exactly 14 digits.")
    private String accountNumber;

    @Pattern(regexp = "^PK\\d{2}[A-Z]{4}\\d{16}$", message = "Invalid IBAN format.")
    private String iban;

    @NotBlank(message = "Account title is required.")
    private String accountTitle;

    @NotBlank(message = "User CNIC is required to link the account.")
    @Pattern(regexp = "^\\d{13}$", message = "User CNIC must be exactly 13 digits.")
    private String userCnicNo;
}