package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FotoRequest {
    @NotBlank
    private String fotoBase64;
}
