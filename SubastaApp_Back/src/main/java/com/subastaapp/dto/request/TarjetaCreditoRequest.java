package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TarjetaCreditoRequest extends MedioPagoRequest {
    @NotBlank
    private String numeroTarjeta;
    @NotBlank
    private String nombreTitular;
    @NotBlank
    private String fechaVencimiento;
    @NotBlank
    private String cvv;
}
