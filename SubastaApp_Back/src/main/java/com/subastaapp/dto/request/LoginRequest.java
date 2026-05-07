package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String documento;
    @NotBlank
    private String password;
}
