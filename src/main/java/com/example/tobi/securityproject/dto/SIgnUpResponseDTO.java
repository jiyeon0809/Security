package com.example.tobi.securityproject.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SIgnUpResponseDTO {
    private String message;
    private String url;
}
