package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CatalogoRequest {
    @NotBlank
    private String descripcion;
    private Long subasta;
    @NotNull
    private Long creadorUsuarioId;
}
