package com.subastaapp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

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

    // Fotos del producto (mínimo 6)
    @NotNull(message = "Se requieren al menos 6 fotos")
    @Size(min = 6, message = "Se requieren al menos 6 fotos")
    @Valid
    private List<FotoRequest> fotos;
}
