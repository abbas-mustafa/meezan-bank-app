package com.example.forgetpassword.mbl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.forgetpassword.mbl.dto.account.AccountCreationRequestDTO;
import com.example.forgetpassword.mbl.dto.account.AccountResponseDTO;
import com.example.forgetpassword.mbl.dto.user.UserResponseDTO;
import com.example.forgetpassword.mbl.entity.AccountEntity;
import com.example.forgetpassword.mbl.entity.UserEntity;
import com.example.forgetpassword.mbl.entity.view.AccountSummaryView;
import com.example.forgetpassword.mbl.exception.ResourceNotFoundException;
import com.example.forgetpassword.mbl.repository.AccountRepository;
import com.example.forgetpassword.mbl.repository.AccountSummaryRepository;
import com.example.forgetpassword.mbl.repository.UserRepository;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountSummaryRepository accountSummaryRepository;
    private final EncryptionService encryptionService;
    private final boolean clientSideEncryptionEnabled;

    @Autowired
    public AccountService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            AccountSummaryRepository accountSummaryRepository,
            EncryptionService encryptionService,
            @Value("${app.security.client-side-encryption-enabled}") boolean clientSideEncryptionEnabled) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountSummaryRepository = accountSummaryRepository;
        this.encryptionService = encryptionService;
        this.clientSideEncryptionEnabled = clientSideEncryptionEnabled;
    }

    public UserEntity findUserByCnicNo(String cnicNo) {
        return userRepository.findById(cnicNo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for CNIC: " + cnicNo));
    }

    public UserResponseDTO verifyCnic(String cnicNo) {
        UserEntity user = findUserByCnicNo(cnicNo);

        return UserResponseDTO.builder()
                .cnicNo(user.getCnicNo())
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public AccountResponseDTO verifyAccountToUser(String accountNumber, String cnicNo) {
        AccountEntity account = accountRepository.findByAccountNumberAndUserCnic(accountNumber, cnicNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account number does not match the provided user."));

        return AccountResponseDTO.fromEntity(account);
    }

    @Transactional
    public UserResponseDTO resetPassword(String cnicNo, String passwordPayload) {
        String rawNewPassword = passwordPayload;

        if (clientSideEncryptionEnabled) {
            rawNewPassword = encryptionService.decrypt(passwordPayload);
        }

        String newHashedPassword = passwordEncoder.encode(rawNewPassword);

        userRepository.resetPasswordProcedure(cnicNo, newHashedPassword);

        UserEntity updatedUser = findUserByCnicNo(cnicNo);

        return UserResponseDTO.builder()
                .cnicNo(updatedUser.getCnicNo())
                .name(updatedUser.getName())
                .username(updatedUser.getUsername())
                .role(updatedUser.getRole())
                .build();
    }

    @Transactional
    public AccountResponseDTO registerNewAccount(AccountCreationRequestDTO request) {
        accountRepository.createAccountForUser(
                request.getUserCnicNo(),
                request.getAccountNumber(),
                request.getAccountTitle(),
                request.getAccountType()
        );

        return accountRepository.findById(request.getAccountNumber())
                .map(AccountResponseDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve account after creation."));
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        if (!accountRepository.existsById(accountNumber)) {
            throw new ResourceNotFoundException("Account not found with number: " + accountNumber);
        }
        accountRepository.deleteById(accountNumber);
    }

    public List<AccountSummaryView> getAccountSummaryByCnic(String cnicNo) {
        List<AccountSummaryView> summaryData = accountSummaryRepository.findByCnicNo(cnicNo);
        if (summaryData.isEmpty()) {
            throw new ResourceNotFoundException("No accounts found for the provided CNIC.");
        }
        return summaryData;
    }

    public List<AccountSummaryView> getAllAccountSummaries() {
        return accountSummaryRepository.findAll();
    }
}