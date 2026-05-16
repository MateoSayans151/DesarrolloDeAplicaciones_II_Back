package com.subastaapp.dto.request;

import com.subastaapp.model.MedioPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioRegistroRequest {
    @NotBlank
    private String documento;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @NotBlank
    private String domicilio;
    @NotBlank
    private String pais;
    @NotBlank
    private String password;
    @NotBlank
    private String FotoDocumentoFrente;
    @NotBlank
    private String FotoDocumentoDorso;
    private String fotoPerfil;
    @NotEmpty
    private List<MedioPagoRequest> medioPagos;
}
