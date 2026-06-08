package com.subastaapp.dto.request;

import com.subastaapp.model.Subasta;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SubastaRequest {
    @NotNull
    private LocalDate fecha;
    @NotNull
    private LocalTime hora;
    private Subasta.EstadoSubasta estado;
    private Long creadorUsuarioId;
    private String ubicacion;
    private Integer capacidadAsistentes;
    private Subasta.OpcionSiNo tieneDeposito;
    private Subasta.OpcionSiNo seguridadPropia;
    @NotNull
    private Subasta.CategoriaSubasta categoria;
    private Integer tiempoItemSegundos;
}
