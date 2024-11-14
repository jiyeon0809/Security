package com.example.tobi.securityproject.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
public class SignInRequestDTO {
    private String userId;
    private String password;
}
