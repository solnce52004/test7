package dev.example.config.security.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TokenRefreshRequestDTO {
    @NotBlank
    private String refreshToken;
}
