package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRegistroRequest {
    @NotBlank
    private String documento;
    @NotBlank
    private String nombre;
    private String direccion;
    @NotBlank
    private String password;
}
