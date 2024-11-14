package com.example.tobi.securityproject.model;

import com.example.tobi.securityproject.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member {
    private long id;
    private String userId;
    private String password;
    private String userName;
    private Role role;
}
