package com.example.CodeGeneratieRestAPI.models;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {

    USER,
    EMPLOYEE;

    @Override
    public String getAuthority() {
        return name();
    }
}
