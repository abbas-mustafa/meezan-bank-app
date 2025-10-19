package com.example.forgetpassword.mbl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.forgetpassword.mbl.dto.ApiResponse;
import com.example.forgetpassword.mbl.dto.account.AccountCreationRequestDTO;
import com.example.forgetpassword.mbl.dto.account.AccountResponseDTO;
import com.example.forgetpassword.mbl.dto.account.FlowVerificationRequestDTO;
import com.example.forgetpassword.mbl.dto.user.UserResponseDTO;
import com.example.forgetpassword.mbl.entity.view.AccountSummaryView;
import com.example.forgetpassword.mbl.service.AccountService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
@RequestMapping("/api/account-flow")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/verify-cnic")
    public ResponseEntity<ApiResponse<UserResponseDTO>> verifyCnic(@RequestBody FlowVerificationRequestDTO request) {
        UserResponseDTO userResponse = accountService.verifyCnic(request.getCnicNo());
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "CNIC verified successfully.", userResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-account")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> verifyAccount(@RequestBody FlowVerificationRequestDTO request) {
        AccountResponseDTO accountResponse = accountService.verifyAccountToUser(request.getAccountNumber(), request.getCnicNo());
        ApiResponse<AccountResponseDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "Account verified successfully.", accountResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<UserResponseDTO>> resetPassword(@RequestBody FlowVerificationRequestDTO request) {
        UserResponseDTO updatedUser = accountService.resetPassword(request.getCnicNo(), request.getNewPassword());
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(HttpStatus.OK.value(), "Password has been reset successfully.", updatedUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-account")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> registerNewAccount(@Valid @RequestBody AccountCreationRequestDTO request) {
        AccountResponseDTO savedAccountDto = accountService.registerNewAccount(request);
        ApiResponse<AccountResponseDTO> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Account registered successfully.", savedAccountDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{accountNumber}")
    public ResponseEntity<ApiResponse<Object>> deleteAccount(@PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Account deleted successfully.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary/{cnicNo}")
    public ResponseEntity<ApiResponse<List<AccountSummaryView>>> getAccountSummary(@PathVariable String cnicNo) {
        List<AccountSummaryView> summaryData = accountService.getAccountSummaryByCnic(cnicNo);

        if (summaryData.isEmpty()) {
            ApiResponse<List<AccountSummaryView>> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "No accounts found for the provided CNIC.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ApiResponse<List<AccountSummaryView>> response = new ApiResponse<>(HttpStatus.OK.value(), "Account summary retrieved successfully.", summaryData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary/all")
    public ResponseEntity<ApiResponse<List<AccountSummaryView>>> getAllAccountSummaries() {
        List<AccountSummaryView> allSummaryData = accountService.getAllAccountSummaries();

        ApiResponse<List<AccountSummaryView>> response = new ApiResponse<>(HttpStatus.OK.value(), "All account summaries retrieved successfully.", allSummaryData);
        return ResponseEntity.ok(response);
    }
}