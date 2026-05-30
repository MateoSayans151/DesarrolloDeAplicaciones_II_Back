package com.subastaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRegistroInicialRequest {
    @NotBlank
    private String documento;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @NotBlank
    private String localidad;
    @NotBlank
    private String calle;
    @NotNull
    private int numeroCalle;
    @NotNull
    private int codigoPostal;
    @NotBlank
    private String pais;
    @NotBlank
    private String fotoDocumentoFrente;
    @NotBlank
    private String fotoDocumentoDorso;
}
