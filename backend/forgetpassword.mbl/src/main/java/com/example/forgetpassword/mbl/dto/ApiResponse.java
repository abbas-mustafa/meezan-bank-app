package com.example.forgetpassword.mbl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    // Constructor for success responses
    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Constructor for error or no-data responses
    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}