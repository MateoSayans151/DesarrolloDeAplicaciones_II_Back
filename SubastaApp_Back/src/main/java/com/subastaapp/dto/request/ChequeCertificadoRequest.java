package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChequeCertificadoRequest extends MedioPagoRequest {
    @NotBlank
    private Double monto;
    @NotBlank
    private String bancoEmisor;
    @NotBlank
    private String titularCheque;
    @NotBlank
    private String numeroCheque;
    @NotBlank
    private String fechaEmision;
    @NotBlank
    private String fotoTitularCheque;
}
