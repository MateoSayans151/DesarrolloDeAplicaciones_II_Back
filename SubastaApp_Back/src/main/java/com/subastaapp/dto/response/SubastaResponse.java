package com.subastaapp.dto.response;

import com.subastaapp.model.Subasta;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SubastaResponse {
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private Long creadorUsuarioId;
    private String categoria;
    private String ubicacion;
    private Integer capacidadAsistentes;

    public static SubastaResponse from(Subasta s) {
        SubastaResponse r = new SubastaResponse();
        r.setId(s.getId());
        r.setFecha(s.getFecha());
        r.setHora(s.getHora());
        r.setEstado(s.getEstado().name());
        r.setCreadorUsuarioId(s.getCreadorUsuario().getId());
        r.setCategoria(s.getCategoria().name());
        r.setUbicacion(s.getUbicacion());
        r.setCapacidadAsistentes(s.getCapacidadAsistentes());
        return r;
    }
}
