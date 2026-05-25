package com.subastaapp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioRegistroFinalRequest {
    @NotBlank
    private String password;
    @NotEmpty
    @Valid
    private List<MedioPagoRequest> medioPagos;
}
