package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RechazoProductoRequest {
    @NotBlank(message = "El motivo de rechazo es obligatorio")
    private String motivoRechazo;
}
