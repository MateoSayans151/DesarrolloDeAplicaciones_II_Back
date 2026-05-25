package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductoRequest {
    private LocalDate fecha;
    @NotBlank
    private String descripcionCompleta;
    private Long propietarioUsuarioId;
    
    // Campos de Seguro
    private String polizaSeguro;
    private String aseguradora;
    private java.math.BigDecimal montoAsegurado;

    // Metadata del bien
    private String artista;
    private String disenador;
    private String historia;
    private String ubicacionDeposito;

    // Declaraciones
    private Boolean origenLicitoDeclarado;
    private Boolean propietarioDeclarado;
}
