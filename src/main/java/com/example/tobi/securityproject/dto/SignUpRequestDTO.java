package com.example.tobi.securityproject.dto;

import com.example.tobi.securityproject.model.Member;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public class SignUpRequestDTO {
    private String userName;
    private String password;
    private String userId;

    public Member toMember(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .userName(userName)
                .password(bCryptPasswordEncoder.encode(password))
                .userId(userId)
                .build();
    }
}
