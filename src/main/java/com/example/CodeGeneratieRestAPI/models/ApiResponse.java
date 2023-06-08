package com.example.CodeGeneratieRestAPI.models;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    //  A constructor that leaves data empty
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    //  A constructor that takes a data object
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
