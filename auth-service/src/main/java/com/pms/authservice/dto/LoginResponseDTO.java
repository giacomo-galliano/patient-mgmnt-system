package com.pms.authservice.dto;

import lombok.Getter;

@Getter
public class LoginResponseDTO {

    // once it's initialized, it cannot be overridden
    private final String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }
}
