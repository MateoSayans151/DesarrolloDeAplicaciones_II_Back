package com.subastaapp.dto.request;

import com.subastaapp.model.Producto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductoRequest {
    private LocalDate fecha;
    private Producto.DisponibilidadProducto disponible;
    private String descripcionCatalogo;
    @NotBlank
    private String descripcionCompleta;
    private Long propietarioUsuarioId;
    private String seguro;
}
