package com.subastaapp.dto.request;

import lombok.Data;

@Data
public class UsuarioUpdateRequest {
    private String nombre;
    private String direccion;
    private String fotoBase64;
}
