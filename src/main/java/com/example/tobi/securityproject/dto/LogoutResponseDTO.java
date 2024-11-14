package com.example.tobi.securityproject.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResponseDTO {
    String message;
    String url;
}
