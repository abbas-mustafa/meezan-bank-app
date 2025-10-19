package com.example.forgetpassword.mbl.exception;

import com.example.forgetpassword.mbl.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountAlreadyExists(AccountAlreadyExistsException ex) {
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationFailed(AuthenticationFailedException ex) {
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Validation failed. Please check your input.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException ex) {
        Throwable rootCause = ex.getMostSpecificCause();
        String rootMessage = rootCause.getMessage() != null ? rootCause.getMessage().toLowerCase() : "";

        StringBuilder finalMessage = new StringBuilder();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Check for specific stored procedure messages and build the final message
        if (rootMessage.contains("username is already taken")) {
            finalMessage.append("Username is already taken. ");
            status = HttpStatus.CONFLICT;
        }
        if (rootMessage.contains("cnic number is already registered")) {
            finalMessage.append("CNIC number is already registered. ");
            status = HttpStatus.CONFLICT;
        }
        if (rootMessage.contains("account number already exists")) {
            finalMessage.append("Account number already exists. ");
            status = HttpStatus.CONFLICT;
        }
        if (rootMessage.contains("account title is already in use")) {
            finalMessage.append("Account title is already in use.");
            status = HttpStatus.CONFLICT;
        }
        if (rootMessage.contains("user with this cnic does not exist")) {
            finalMessage.append("Cannot create account: The specified user does not exist. ");
            status = HttpStatus.NOT_FOUND;
        }

        // If no specific message was found, use a generic one.
        if (finalMessage.isEmpty()) {
            finalMessage.append("A database error occurred.");
        }

        ApiResponse<Object> response = new ApiResponse<>(status.value(), finalMessage.toString().trim());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex) {
        logger.error("An unexpected internal error occurred: ", ex);

        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected internal server error occurred."
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DecryptionFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handleDecryptionFailed(DecryptionFailedException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Failed to process the request. The provided data may be invalid or corrupt."
        );
        // Log the underlying cause for debugging
        logger.error("Decryption failed: {}", ex.getCause().getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}