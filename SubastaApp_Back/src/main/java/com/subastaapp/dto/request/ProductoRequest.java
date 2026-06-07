package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductoRequest {
    private String fecha;
    @NotBlank
    private String descripcionCompleta;
    private Long propietarioUsuarioId;
    
    // Campos de Seguro
    private String polizaSeguro;
    private String aseguradora;
    private java.math.BigDecimal montoAsegurado;
    private String monedaAsegurado;

    // Metadata del bien
    private String artista;
    private String disenador;
    private String historia;
    private String ubicacionDeposito;

    // Declaraciones
    private Boolean origenLicitoDeclarado;
    private Boolean propietarioDeclarado;
}
