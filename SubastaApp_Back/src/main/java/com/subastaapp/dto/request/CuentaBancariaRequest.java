package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CuentaBancariaRequest extends MedioPagoRequest {
    @NotBlank
    private String titularCuenta;
    @NotBlank
    private String tipoCuenta;
    @NotBlank
    private String moneda;
    @NotBlank
    private String cbuAlias;
}
