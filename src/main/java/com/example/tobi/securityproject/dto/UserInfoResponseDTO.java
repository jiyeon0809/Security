package com.example.tobi.securityproject.dto;


import com.example.tobi.securityproject.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDTO {
    private long id;
    private String userId;
    private String userName;
    private Role role;
}