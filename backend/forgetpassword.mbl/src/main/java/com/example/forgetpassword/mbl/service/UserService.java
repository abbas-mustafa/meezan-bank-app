package com.example.forgetpassword.mbl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.forgetpassword.mbl.dto.user.UserRegistrationRequestDTO;
import com.example.forgetpassword.mbl.dto.user.UserResponseDTO;
import com.example.forgetpassword.mbl.entity.UserEntity;
import com.example.forgetpassword.mbl.exception.AuthenticationFailedException;
import com.example.forgetpassword.mbl.exception.ResourceNotFoundException;
import com.example.forgetpassword.mbl.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final boolean clientSideEncryptionEnabled;

    @Autowired
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EncryptionService encryptionService,
            @Value("${app.security.client-side-encryption-enabled}") boolean clientSideEncryptionEnabled) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
        this.clientSideEncryptionEnabled = clientSideEncryptionEnabled;
    }

    @Transactional
    public UserResponseDTO registerNewUser(UserRegistrationRequestDTO request) {
        String rawPassword = request.getPassword();
        
        if (clientSideEncryptionEnabled) {
            rawPassword = encryptionService.decrypt(request.getPassword());
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);

        userRepository.registerUserProcedure(
                request.getCnicNo(),
                request.getUsername(),
                request.getName(),
                hashedPassword
        );

        UserEntity registeredUser = userRepository.findById(request.getCnicNo())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve user after registration."));

        return UserResponseDTO.builder()
                .cnicNo(registeredUser.getCnicNo())
                .name(registeredUser.getName())
                .username(registeredUser.getUsername())
                .role(registeredUser.getRole())
                .build();
    }

    public UserResponseDTO authenticateUser(String username, String passwordFromClient) {
        String rawPassword = passwordFromClient;

        if (clientSideEncryptionEnabled) {
            rawPassword = encryptionService.decrypt(passwordFromClient);
        }
        
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid username or password."));

        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return UserResponseDTO.builder()
                    .cnicNo(user.getCnicNo())
                    .name(user.getName())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();
        } else {
            throw new AuthenticationFailedException("Invalid username or password.");
        }
    }

    @Transactional
    public void deleteUser(String cnicNo) {
        if (!userRepository.existsById(cnicNo)) {
            throw new ResourceNotFoundException("User not found with CNIC: " + cnicNo);
        }
        userRepository.deleteById(cnicNo);
    }
}